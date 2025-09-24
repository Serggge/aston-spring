package ru.serggge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.serggge.config.MapperConfig;
import ru.serggge.conrollers.UserController;
import ru.serggge.dto.CreateRequest;
import ru.serggge.dto.UpdateRequest;
import ru.serggge.entity.User;
import ru.serggge.exception.UserNotFoundException;
import ru.serggge.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(MapperConfig.class)
public class UserControllerWebLayerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UserService userService;
    User user;
    @Captor
    ArgumentCaptor<User> captor;

    @BeforeEach
    void setUp() {
        Long userId = ThreadLocalRandom.current()
                                       .nextLong(1, 100);
        String username = "User" + ThreadLocalRandom.current()
                                                    .nextInt();
        String email = username + "@email.org";
        int age = ThreadLocalRandom.current()
                                   .nextInt(1, 100);
        user = new User(userId, username, email, age, Instant.now());
    }

    @Test
    @DisplayName("Create new - success")
    @SneakyThrows
    void createNewUserTest_successBehavior_statusCreated() {
        final CreateRequest dto = new CreateRequest(user.getName(), user.getEmail(), user.getAge());
        final String json = objectMapper.writeValueAsString(dto);
        when(userService.createUser(captor.capture())).thenReturn(user);

        mockMvc
                .perform(post("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.id", is(user.getId()), Long.class),
                        jsonPath("$.name", equalTo(user.getName())),
                        jsonPath("$.email", equalTo(user.getEmail())),
                        jsonPath("$.age", is(user.getAge()))
                );

        verify(userService, times(1)).createUser(captor.getValue());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Fail Create - Duplicate email")
    @SneakyThrows
    void createNewUserTest_emailIsNotUnique_statusBadRequest() {
        final CreateRequest dto = new CreateRequest(user.getName(), user.getEmail(), user.getAge());
        final String json = objectMapper.writeValueAsString(dto);
        final String errorMessage = "Email already exists";
        when(userService.createUser(captor.capture())).thenThrow(new IllegalArgumentException(errorMessage));

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

        verify(userService, times(1)).createUser(captor.getValue());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Fail Create - null field")
    @SneakyThrows
    void createNewUserTest_failOnNullProperty_statusBadRequest() {
        final CreateRequest dto = new CreateRequest(null, user.getEmail(), user.getAge());
        final String json = objectMapper.writeValueAsString(dto);
        final String errorMessage = "request validation error";
        when(userService.createUser(captor.capture())).thenReturn(user);

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

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Fail Create - wrong email format")
    @SneakyThrows
    void createNewUserTest_failOnBadEmailFormat_statusBadRequest() {
        final CreateRequest dto = new CreateRequest(user.getName(), "email_bad_format", user.getAge());
        final String json = objectMapper.writeValueAsString(dto);
        final String errorMessage = "request validation error";
        when(userService.createUser(captor.capture())).thenReturn(user);

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

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Fail Create - negative age")
    @SneakyThrows
    void createNewUserTest_failOnNegativeAge_statusBadRequest() {
        final CreateRequest dto = new CreateRequest(user.getName(), user.getEmail(), -1);
        final String json = objectMapper.writeValueAsString(dto);
        final String errorMessage = "request validation error";
        when(userService.createUser(captor.capture())).thenReturn(user);

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

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Update user - success")
    @SneakyThrows
    void updateUser_successBehavior_statusOk() {
        final UpdateRequest dto = new UpdateRequest(user.getName(), user.getEmail(), user.getAge());
        final String json = objectMapper.writeValueAsString(dto);
        when(userService.updateUser(captor.capture())).thenReturn(user);

        mockMvc
                .perform(patch("/users")
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.id", is(user.getId()), Long.class),
                        jsonPath("$.name", equalTo(user.getName())),
                        jsonPath("$.email", equalTo(user.getEmail())),
                        jsonPath("$.age", is(user.getAge()))
                );
        verify(userService, times(1)).updateUser(captor.getValue());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Fail Update - email not found")
    @SneakyThrows
    void updateUser_emailNotFound_statusNotFound() throws JsonProcessingException {
        UpdateRequest dto = new UpdateRequest(user.getName(), user.getEmail(), user.getAge());
        final String json = objectMapper.writeValueAsString(dto);
        final String errorMessage = "Email not found";
        when(userService.updateUser(captor.capture())).thenThrow(new UserNotFoundException(errorMessage));

        mockMvc
                .perform(patch("/users")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        verify(userService, times(1)).updateUser(captor.getValue());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Find user - success")
    @SneakyThrows
    void findUserById_successBehavior_statusOk() {
        final String requestUri = "/users/" + user.getId();
        when(userService.getUser(anyLong())).thenReturn(user);

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

        verify(userService, times(1)).getUser(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Fail Find - unknown ID")
    @SneakyThrows
    void findUserById_userIdNotFound_statusNotFound() {
        final String requestUri = "/users/" + user.getId();
        final String errorMessage = "User not found";
        when(userService.getUser(anyLong())).thenThrow(new UserNotFoundException(errorMessage));

        mockMvc
                .perform(get(requestUri))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        verify(userService, times(1)).getUser(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Find group - Size 2/2")
    @SneakyThrows
    void findGroupUsers_statusOk() {
        final User john = new User("john", "john@email.org", 20);
        final User katty = new User("Katty", "katty@email.org", 22);
        List<User> users = List.of(john, katty);
        when(userService.getGroup(anyInt(), anyInt())).thenReturn(users);

        mockMvc
                .perform(get("/users")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$", hasSize(users.size())),
                        jsonPath("$[*].name", containsInAnyOrder(john.getName(), katty.getName())),
                        jsonPath("$[*].email", containsInAnyOrder(john.getEmail(), katty.getEmail())),
                        jsonPath("$[*].age", containsInAnyOrder(john.getAge(), katty.getAge()))
                );

        verify(userService, times(1)).getGroup(0, 2);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Delete user - success")
    @SneakyThrows
    void deleteUser_successBehavior_statusNoContent() {
        final String requestUri = "/users/" + user.getId();
        doNothing().when(userService)
                   .removeUser(anyLong());

        mockMvc
                .perform(delete(requestUri))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).removeUser(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Fail Delete - unknown ID")
    @SneakyThrows
    void deleteUser_userIdNotFound_statusNotFound() {
        final String requestUri = "/users/" + user.getId();
        final String errorMessage = "User not found";
        doThrow(new UserNotFoundException("User not found")).when(userService)
                                                            .removeUser(anyLong());

        mockMvc
                .perform(delete(requestUri))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.message", equalTo(errorMessage))
                );

        verify(userService, times(1)).removeUser(user.getId());
        verifyNoMoreInteractions(userService);
    }
}
