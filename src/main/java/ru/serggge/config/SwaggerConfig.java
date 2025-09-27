package ru.serggge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicUserApi() {
        return GroupedOpenApi.builder()
                             .group("Users")
                             .pathsToMatch("/users/**")
                             .build();
    }

    @Bean
    public OpenAPI customOpenApi(SwaggerProperties properties) {
        return new OpenAPI().info(
                                    new Info().title(properties.getAppName())
                                              .version(properties.getAppVersion())
                                              .description(properties.getAppDescription()))
                            .servers(List.of(new Server().url("http://localhost:8080")
                                                         .description("Dev service"),
                                    new Server().url("http://localhost:8082")
                                                .description("Beta service")));
    }
}