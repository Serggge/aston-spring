package ru.serggge.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.serggge.entity.Credentials;
import ru.serggge.entity.Role;
import ru.serggge.exception.AuthorizationException;
import ru.serggge.repository.CredentialsRepository;
import javax.security.sasl.AuthenticationException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class AdminAuthorityInterceptor implements HandlerInterceptor {

    private final CredentialsRepository repository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        // перехватчик обрабатывает только эндпоинты с методом DELETE
        if (!method.equals(HttpMethod.DELETE.toString())) {
            return true;
        }
        // разрешаем выполнить метод только пользователю предоставившему креденшелы админа
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Basic ")) {
            throw new AuthenticationException("Request is not authenticated");
        }
        try {
            String encodedCredentials = authorization.substring(6);
            byte[] decodeBytes = Base64.getDecoder()
                                       .decode(encodedCredentials);
            String rawCredentials = new String(decodeBytes, StandardCharsets.UTF_8);
            String[] usernamePassword = rawCredentials.split(":");
            Credentials userCredentials = repository.findByUsername(usernamePassword[0])
                                                .orElseThrow(() -> new AuthorizationException("Bad credentials"));

            if (userCredentials.getRole().equals(Role.ROLE_ADMIN)
                    && userCredentials.getPassword().equals(usernamePassword[1])) {
                return true;
            } else {
                throw new AuthorizationException("Bad credentials");
            }
        } catch (RuntimeException e) {
            throw new AuthorizationException("Bad credentials");
        }
    }
}