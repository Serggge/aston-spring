package ru.serggge.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.serggge.repository.CredentialsRepository;

@Slf4j
@RequiredArgsConstructor
public class AdminAuthorityInterceptor implements HandlerInterceptor {

    private final CredentialsRepository repository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
}