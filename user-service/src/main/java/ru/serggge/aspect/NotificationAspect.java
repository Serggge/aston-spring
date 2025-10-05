package ru.serggge.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import ru.serggge.model.Event;
import ru.serggge.entity.User;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class NotificationAspect {

    @Around(value = "@annotation(Notification)")
    public Object notificate(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Notification annotation = methodSignature.getMethod()
                                                 .getDeclaredAnnotation(Notification.class);
        Event event = annotation.event();

        return switch (event) {
            case CREATE -> handleCreateNotification(joinPoint);
            case UPDATE -> handleUpdateNotification(joinPoint);
            case DELETE -> handleDeleteNotification(joinPoint);
        };
    }

    private Object handleCreateNotification(ProceedingJoinPoint joinPoint) throws Throwable {
        User user = (User) joinPoint.proceed();
        log.info("User created: {}", user);
        return user;
    }

    private Object handleUpdateNotification(ProceedingJoinPoint joinPoint) throws Throwable {
        User user = (User) joinPoint.proceed();
        log.info("User updated: {}", user);
        return user;
    }

    private Object handleDeleteNotification(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = (Long) joinPoint.getArgs()[0];
        joinPoint.proceed();
        log.info("User with ID <{}> deleted", userId);
        return null;
    }
}