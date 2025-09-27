package ru.serggge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.serggge.config.TestConfig;
import ru.serggge.dto.CreateUserRequestDto;
import ru.serggge.dto.FindUserResponseDto;
import ru.serggge.dto.UpdateUserRequestDto;
import ru.serggge.entity.User;
import ru.serggge.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Import(TestConfig.class)
@Testcontainers
@Transactional
@Sql(scripts = "/sql_script/test_schema.sql", executionPhase = BEFORE_TEST_CLASS,
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED))
public class UserControllerApplicationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    @Container
    PostgreSQLContainer<?> postgres;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Create new - success")
    @SneakyThrows
    void createNewUser_successBehavior_returnStatusCreated() {
        final String username = generateUserName();
        final String email = generateEmail(username);
        final int age = generateAge();
        final CreateUserRequestDto request = new CreateUserRequestDto(username, email, age);
        final String json = objectMapper.writeValueAsString(request);

        mockMvc
                .perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .accept(APPLICATION_JSON))
                .andExpectAll(status().isCreated(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.id", instanceOf(Number.class)),
                        jsonPath("$.name", equalTo(username)),
                        jsonPath("$.email", equalTo(email)),
                        jsonPath("$.age", is(age)),
                        jsonPath("$.createdAt", notNullValue())
                );

        // проверяем наличие записи в БД
        Optional<User> checkedUser = userRepository.findByEmail(email);

        assertTrue(checkedUser.isPresent());
    }

    @Test
    @DisplayName("Fail Create - Duplicate email")
    @Sql(statements = "INSERT INTO users (name, email, age) VALUES ('John', 'john@email.org', 20)")
    @SneakyThrows
    void createNewUserTest_emailIsNotUnique_statusBadRequest() {
        final String json = objectMapper.writeValueAsString(
                new CreateUserRequestDto("Bill", "john@email.org", 25));

        mockMvc
                .perform(post("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo("Email already exists"))
                );

        // проверяем, что в БД не сохранился пользователь с таким же email
        Optional<User> checkedUser = userRepository.findByEmail("john@email.org");

        assertTrue(checkedUser.isPresent());
        assertThat(checkedUser.get()
                              .getName(), not("Bill"));
    }

    @Test
    @DisplayName("Fail Create - null field")
    @SneakyThrows
    void createNewUserTest_failOnNullProperty_statusBadRequest() {
        final String email = "user@email.org";
        final int age = generateAge();
        final CreateUserRequestDto request = new CreateUserRequestDto(null, email, age);
        final String json = objectMapper.writeValueAsString(request);
        final String errorMessage = "request validation error";

        mockMvc
                .perform(post("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        // проверяем, что user не сохранился в БД
        Optional<User> checkedUser = userRepository.findByEmail(email);

        assertTrue(checkedUser.isEmpty());
    }

    @Test
    @DisplayName("Fail Create - wrong email format")
    @SneakyThrows
    void createNewUserTest_failOnBadEmailFormat_statusBadRequest() {
        final String userName = generateUserName();
        final String email = "bad_email_format";
        final int age = generateAge();
        final CreateUserRequestDto request = new CreateUserRequestDto(userName, email, age);
        final String json = objectMapper.writeValueAsString(request);
        final String errorMessage = "request validation error";

        mockMvc
                .perform(post("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        // проверяем, что user не сохранился в БД
        Optional<User> checkedUser = userRepository.findByEmail(email);

        assertTrue(checkedUser.isEmpty());
    }

    @Test
    @DisplayName("Fail Create - negative age")
    @SneakyThrows
    void createNewUserTest_failOnNegativeAge_statusBadRequest() {
        final String userName = generateUserName();
        final String email = generateEmail(userName);
        final int age = -1;
        final CreateUserRequestDto request = new CreateUserRequestDto(userName, email, age);
        final String json = objectMapper.writeValueAsString(request);
        final String errorMessage = "request validation error";

        mockMvc
                .perform(post("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo("request validation error"))
                );

        // проверяем, что user не сохранился в БД
        Optional<User> checkedUser = userRepository.findByEmail(email);

        assertTrue(checkedUser.isEmpty());
    }

    @Test
    @DisplayName("Update user - success")
    @Sql(statements = "INSERT INTO users (name, email, age) VALUES ('John', 'john@email.org', 20)")
    @SneakyThrows
    void updateUser_successBehavior_statusOk() {
        final String json = objectMapper.writeValueAsString(
                new UpdateUserRequestDto("Bill", "john@email.org", 21));

        mockMvc
                .perform(patch("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.name", equalTo("Bill")),
                        jsonPath("$.email", equalTo("john@email.org")),
                        jsonPath("$.age", is(21))
                );

        // проверяем, что в БД user с обновлёнными полями name\age, но прежним ID
        Optional<User> checkedUser = userRepository.findByEmail("john@email.org");

        assertTrue(checkedUser.isPresent());

        User persistenceUser = checkedUser.get();
        assertAll("Check user properties",
                () -> assertThat(persistenceUser.getName(), equalTo("Bill")),
                () -> assertThat(persistenceUser.getAge(), is(21))
        );
    }

    @Test
    @DisplayName("Fail Update - email not found")
    @SneakyThrows
    void updateUser_emailNotFound_statusNotFound() {
        final String email = "john@email.org";
        final UpdateUserRequestDto request = new UpdateUserRequestDto("john", email, 20);
        final String json = objectMapper.writeValueAsString(request);
        final String errorMessage = "Email not found";

        mockMvc
                .perform(patch("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        // проверяем, что user не сохранился в БД
        Optional<User> checkedUser = userRepository.findByEmail(email);

        assertTrue(checkedUser.isEmpty());
    }

    @Test
    @DisplayName("Find user - success")
    @Sql(statements = "INSERT INTO users (name, email, age) VALUES ('John', 'john@email.org', 20)")
    @SneakyThrows
    void findUserById_successBehavior_statusOk() {
        User user = userRepository.findByEmail("john@email.org")
                                  .get();

        final String requestUri = "/users/" + user.getId();

        mockMvc
                .perform(get(requestUri)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.id", is(user.getId()), Long.class),
                        jsonPath("$.name", equalTo(user.getName())),
                        jsonPath("$.email", equalTo(user.getEmail())),
                        jsonPath("$.age", is(user.getAge()))
                );
    }

    @Test
    @DisplayName("Fail Find - unknown ID")
    @SneakyThrows
    void findUserById_userIdNotFound_statusNotFound() {
        final long userId = 0;
        final String requestUri = "/users/" + userId;
        final String errorMessage = "User not found";

        mockMvc
                .perform(get(requestUri))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        // проверяем, что в БД действительно нет пользователя с таким ID
        Optional<User> checkedUser = userRepository.findById(userId);

        assertTrue(checkedUser.isEmpty());
    }

    @Test
    @DisplayName("Find group - default uri params")
    @SneakyThrows
    void findAllUsers_defaultUriParams_statusOk() {
        final int defaultPageSize = 10;
        // предварительное сохранение в БД пользователей, по которым в дальнейшем будет осуществляться выборка
        final List<User> users = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            String userName = "User" + i;
            String email = userName + "@email.org";
            users.add(new User(userName, email, i));
        }
        userRepository.saveAll(users);

        // проверяем, что в БД попали все пользователи
        List<User> checkedList = userRepository.findAll();
        assertThat(checkedList, hasSize(users.size()));

        mockMvc
                .perform(get("/users")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$", hasSize(defaultPageSize))
                );
    }

    @Test
    @DisplayName("Find group - Size 2/3")
    @Sql(scripts = "/sql_script/insert_three_users.sql", executionPhase = BEFORE_TEST_METHOD)
    @SneakyThrows
    void findAllUsers_severalUsers_statusOk() {
        MockHttpServletResponse response = mockMvc
                .perform(get("/users")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(APPLICATION_JSON))
                .andReturn()
                .getResponse();

        CollectionType listType = objectMapper.getTypeFactory()
                                              .constructCollectionType(List.class, FindUserResponseDto.class);
        List<FindUserResponseDto> myObjectList = objectMapper.readValue(response.getContentAsString(), listType);


        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(response.getContentType(), is(APPLICATION_JSON.toString()));
        assertThat(myObjectList, hasSize(2));
    }

    @Test
    @DisplayName("Delete user - success")
    @Sql(statements = "INSERT INTO users (name, email, age) VALUES ('John', 'john@email.org', 20)")
    @SneakyThrows
    void deleteUser_successBehavior_statusNoContent() {
        final long userId = userRepository.findByEmail("john@email.org")
                                          .get()
                                          .getId();

        final String requestUri = String.format("/users/%d", userId);

        mockMvc
                .perform(delete(requestUri)
                        .header(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4="))
                .andExpect(status().isNoContent());

        // проверяем, что пользователь был удалён из БД
        Optional<User> checkedUser = userRepository.findById(userId);

        assertTrue(checkedUser.isEmpty());
    }

    @Test
    @DisplayName("Delete user - fail Authentication")
    @Sql(statements = "INSERT INTO users (name, email, age) VALUES ('John', 'john@email.org', 20)")
    @SneakyThrows
    void deleteUser_failRequestIsNotAuthenticated_statusForbidden() {
        final long userId = userRepository.findByEmail("john@email.org")
                                          .get()
                                          .getId();

        final String requestUri = String.format("/users/%d", userId);

        mockMvc
                .perform(delete(requestUri))
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", is("Request is not authenticated")));

        // проверяем, что пользователь существует в БД
        Optional<User> checkedUser = userRepository.findById(userId);

        assertTrue(checkedUser.isPresent());
    }

    @Test
    @DisplayName("Delete user - fail Authorization")
    @Sql(statements = "INSERT INTO users (name, email, age) VALUES ('John', 'john@email.org', 20)")
    @SneakyThrows
    void deleteUser_failBadCredentials_statusUnauthorized() {
        final long userId = userRepository.findByEmail("john@email.org")
                                          .get()
                                          .getId();

        final String requestUri = String.format("/users/%d", userId);

        mockMvc
                .perform(delete(requestUri)
                        .header(HttpHeaders.AUTHORIZATION, "Basic 12345"))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", is("Bad credentials")));

        // проверяем, что пользователь существует в БД
        Optional<User> checkedUser = userRepository.findById(userId);

        assertTrue(checkedUser.isPresent());
    }

    @Test
    @DisplayName("Fail Delete - unknown ID")
    @SneakyThrows
    void deleteUser_userIdNotFound_statusNotFound() {
        final Long userId = 0L;
        final String requestUri = "/users/" + userId;
        final String errorMessage = "User not found";

        mockMvc
                .perform(delete(requestUri)
                        .header(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4="))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        // проверяем, что в БД действительно нет user с указанным ID
        Optional<User> checkedUser = userRepository.findById(userId);

        assertTrue(checkedUser.isEmpty());
    }

    String generateUserName() {
        return "User" + ThreadLocalRandom.current()
                                         .nextInt();
    }

    String generateEmail(String userName) {
        return userName + "@email.org";
    }

    int generateAge() {
        return ThreadLocalRandom.current()
                                .nextInt(1, 100);
    }

}