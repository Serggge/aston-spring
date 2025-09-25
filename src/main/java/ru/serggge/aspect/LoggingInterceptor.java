package ru.serggge.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@Slf4j
public class LoggingInterceptor {

    @AfterReturning(value = "@annotation(ToLog)", returning = "result")
    public void log(JoinPoint joinPoint, Object returnedValue) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method methodToLog = methodSignature.getMethod()
                                            .getDeclaredAnnotation(ToLog.class)
                                            .method();
        switch (methodToLog) {
            case CREATE -> log.info("User created: {}", returnedValue);
            case UPDATE -> log.info("User updated: {}", returnedValue);
            case DELETE -> {
                Object[] args = joinPoint.getArgs();
                log.info("User with ID: <{}> deleted", args[0]);
            }
        }
    }
}
