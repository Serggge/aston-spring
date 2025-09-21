package ru.serggge;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.serggge.config.WebConfig;
import ru.serggge.conrollers.UserController;
import ru.serggge.dto.CreateRequest;
import ru.serggge.entity.User;
import ru.serggge.service.UserService;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(WebConfig.class)
public class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UserService service;
    User user;
    @Captor
    ArgumentCaptor<User> captor;

    @BeforeEach
    void setUp() {
        String username = "User" + ThreadLocalRandom.current()
                                                    .nextInt();
        String email = username + "@email.org";
        int age = ThreadLocalRandom.current()
                                   .nextInt(1, 100);
        User createdUser = new User(username, email, age);
        createdUser.setId(1L);
        createdUser.setCreatedAt(Instant.now());
        user = createdUser;
    }

    @Test
    @DisplayName("Create new user")
    @SneakyThrows
    void createNewUserTest() {
        CreateRequest request = new CreateRequest(user.getName(), user.getEmail(), user.getAge());
        String json = objectMapper.writeValueAsString(request);
        when(service.createUser(captor.capture())).thenReturn(user);

        mvc
                .perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .accept(APPLICATION_JSON))
                .andExpectAll(status().isCreated(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath("$.id", is(user.getId()), Long.class),
                        jsonPath("$.name", equalTo(user.getName())),
                        jsonPath("$.email", equalTo(user.getEmail())),
                        jsonPath("$.age", is(user.getAge())),
                        jsonPath("$.createdAt", equalTo(user.getCreatedAt().toString()))
                );

        verify(service, times(1)).createUser(captor.getValue());
        verifyNoMoreInteractions(service);
    }


}
