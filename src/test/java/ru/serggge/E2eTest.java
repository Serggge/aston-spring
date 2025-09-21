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
import ru.serggge.config.E2eTestConfig;
import ru.serggge.dto.CreateRequest;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Import(E2eTestConfig.class)
@Testcontainers
@Transactional
public class E2eTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    @Container
    PostgreSQLContainer<?> postgres;

    @Test
    @DisplayName("Create new user")
    @SneakyThrows
    void createNewUserTest() {
        final String username = "John";
        final String email = "john@email.org";
        final int age = 30;
        CreateRequest request = new CreateRequest(username, email, age);
        String json = objectMapper.writeValueAsString(request);

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
    }

}