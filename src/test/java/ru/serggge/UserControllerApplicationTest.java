package ru.serggge;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.serggge.config.TestConfig;
import ru.serggge.dto.CreateRequest;
import ru.serggge.dto.UpdateRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Import(TestConfig.class)
@Testcontainers
@Transactional
public class UserControllerApplicationTest {

    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    @Container
    PostgreSQLContainer<?> postgres;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Create new - success")
    @SneakyThrows
    void createNewUserTest() {
        final String username = generateUserName();
        final String email = generateEmail(username);
        final int age = generateAge();
        final CreateRequest request = new CreateRequest(username, email, age);
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
    @SneakyThrows
    void createNewUserTest_emailIsNotUnique_statusBadRequest() {
        final String userName = generateUserName();
        final String email = generateEmail(userName);
        final int age = generateAge();
        final CreateRequest request = new CreateRequest(userName, email, age);
        final String json = objectMapper.writeValueAsString(request);
        final String errorMessage = "Email already exists";

        // перед выполнением запроса к API, сохраняем в БД user с таким же email
        userRepository.save(new User("OtherUser", email, age + 1));

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

        // проверяем, что в БД не сохранился пользователь с таким же email
        Optional<User> checkedUser = userRepository.findByEmail(email);

        assertTrue(checkedUser.isPresent());
        assertThat(checkedUser.get()
                              .getName(), not(userName));
    }

    @Test
    @DisplayName("Fail Create - null field")
    @SneakyThrows
    void createNewUserTest_failOnNullProperty_statusBadRequest() {
        final String email = "user@email.org";
        final int age = generateAge();
        final CreateRequest request = new CreateRequest(null, email, age);
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
        final CreateRequest request = new CreateRequest(userName, email, age);
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
        final CreateRequest request = new CreateRequest(userName, email, age);
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
    @SneakyThrows
    void updateUser_successBehavior_statusOk() {
        final String username = generateUserName();
        final String email = generateEmail(username);
        final int age = generateAge();

        // перед выполнением теста, сохраняем в БД user, которого будем в последствии обновлять
        final User originalUser = new User(username, email, age);
        User savedUser = userRepository.save(originalUser);

        // изменяем атрибуты user
        final String newName = "NewName";
        final int newAge = age + 1;

        final UpdateRequest request = new UpdateRequest(newName, email, newAge);
        final String json = objectMapper.writeValueAsString(request);

        mockMvc
                .perform(patch("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.id", is(originalUser.getId()), Long.class),
                        jsonPath("$.name", equalTo(newName)),
                        jsonPath("$.email", equalTo(email)),
                        jsonPath("$.age", is(newAge)),
                        jsonPath("$.createdAt", equalTo(savedUser.getCreatedAt()
                                                                 .toString()))
                );

        // проверяем, что в БД user с обновлёнными полями name\age, но прежним ID
        Optional<User> checkedUser = userRepository.findByEmail(email);

        assertTrue(checkedUser.isPresent());

        User persistenceUser = checkedUser.get();
        assertAll("Check user properties",
                () -> assertThat(persistenceUser.getId(), is(savedUser.getId())),
                () -> assertThat(persistenceUser.getName(), equalTo(newName)),
                () -> assertThat(persistenceUser.getAge(), is(newAge))
        );
    }

    @Test
    @DisplayName("Fail Update - email not found")
    @SneakyThrows
    void updateUser_emailNotFound_statusNotFound() {
        final String email = "john@email.org";
        final UpdateRequest request = new UpdateRequest("john", email, 20);
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
    @SneakyThrows
    void findUserById_successBehavior_statusOk() {
        final String username = generateUserName();
        final String email = generateEmail(username);
        final int age = generateAge();

        // перед выполнением теста сохраняем в БД user, которого далее в тесте будем искать по ID
        final User user = new User(username, email, age);
        User savedUser = userRepository.save(user);

        final String requestUri = "/users/" + savedUser.getId();

        mockMvc
                .perform(get(requestUri)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.id", is(savedUser.getId()), Long.class),
                        jsonPath("$.name", equalTo(user.getName())),
                        jsonPath("$.email", equalTo(user.getEmail())),
                        jsonPath("$.age", is(user.getAge())),
                        jsonPath("$.createdAt", equalTo(savedUser.getCreatedAt()
                                                                 .toString()))
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
    void findGroupUsers_defaultUriParams_statusOk() {
        final int defaultPageSize = 10;
        // предварительное сохранение в БД пользователей, по которым в дальнейшем будет осуществляться выборка
        final List<User> users = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            String userName = "User" + i;
            String email = userName + "@email.org";
            int age = i;
            users.add(new User(userName, email, age));
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
    @DisplayName("Find group - Size 2/2")
    @SneakyThrows
    void findGroupUsers_allUsers_statusOk() {
        // предварительное сохранение в БД пользователей, по которым в дальнейшем будет осуществляться выборка
        final int groupSize = 2;
        final User john = new User("john", "john@email.org", 20);
        final User katty = new User("Katty", "katty@email.org", 22);
        List<User> users = List.of(john, katty);
        userRepository.saveAll(users);

        mockMvc
                .perform(get("/users")
                        .param("page", "0")
                        .param("size", String.valueOf(groupSize))
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$", hasSize(groupSize)),
                        jsonPath("$[*].name", containsInAnyOrder(john.getName(), katty.getName())),
                        jsonPath("$[*].email", containsInAnyOrder(john.getEmail(), katty.getEmail())),
                        jsonPath("$[*].age", containsInAnyOrder(john.getAge(), katty.getAge()))
                );
    }

    @Test
    @DisplayName("Find group - Size 2/3")
    @SneakyThrows
    void findGroupUsers_severalUsers_statusOk() {
        // предварительное сохранение в БД пользователей, по которым в дальнейшем будет осуществляться выборка
        final int groupSize = 2;
        final User john = new User("john", "john@email.org", 20);
        final User katty = new User("Katty", "katty@email.org", 22);
        final User bill = new User("Bill", "bill@email.org", 24);
        List<User> users = List.of(john, katty, bill);
        userRepository.saveAll(users);

        mockMvc
                .perform(get("/users")
                        .param("page", "0")
                        .param("size", String.valueOf(groupSize))
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$", hasSize(groupSize)),
                        jsonPath("$[*].name", everyItem(anyOf(
                                equalTo(john.getName()), equalTo(katty.getName()), equalTo(bill.getName())))),
                        jsonPath("$[*].email", everyItem(anyOf(
                                equalTo(john.getEmail()), equalTo(katty.getEmail()), equalTo(bill.getEmail())))),
                        jsonPath("$[*].age", everyItem(anyOf(
                                is(john.getAge()), is(katty.getAge()), is(bill.getAge()))))
                );
    }

    @Test
    @DisplayName("Delete user - success")
    @SneakyThrows
    void deleteUser_successBehavior_statusNoContent() {
        final String username = generateUserName();
        final String email = generateEmail(username);
        final int age = generateAge();

        // перед выполнением теста, сохраняем в БД user, которого далее в тесте будем искать по ID
        final User user = new User(username, email, age);
        User savedUser = userRepository.save(user);

        final String requestUri = "/users/" + savedUser.getId();

        mockMvc
                .perform(delete(requestUri))
                .andExpect(status().isNoContent());

        // проверяем, что пользователь был удалён из БД
        Optional<User> checkedUser = userRepository.findById(savedUser.getId());

        assertTrue(checkedUser.isEmpty());
    }

    @Test
    @DisplayName("Fail Delete - unknown ID")
    @SneakyThrows
    void deleteUser_userIdNotFound_statusNotFound() {
        final Long userId = 0L;
        final String requestUri = "/users/" + userId;
        final String errorMessage = "User not found";

        mockMvc
                .perform(delete(requestUri))
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