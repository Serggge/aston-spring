package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.serggge.model.Event;
import ru.serggge.aspect.Notification;
import ru.serggge.entity.OutboxEvent;
import ru.serggge.entity.User;
import ru.serggge.exception.UserNotFoundException;
import ru.serggge.repository.OutboxRepository;
import ru.serggge.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import static ru.serggge.model.Event.*;
import static ru.serggge.aspect.NotificationType.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;

    @Override
    @Notification(type = LOG, event = CREATE)
    @Transactional
    public User createUser(User user) {
        userRepository.findByEmail(user.getEmail())
                      .ifPresent(u -> {
                      throw new IllegalArgumentException("Email already exists");
                  });
        userRepository.save(user);
        OutboxEvent event = new OutboxEvent(CREATE, user.getEmail());
        outboxRepository.save(event);
        return user;
    }

    @Override
    @Notification(type = LOG, event = Event.UPDATE)
    @Transactional
    public User updateUser(User user) {
        User persistenceUser = userRepository.findByEmail(user.getEmail())
                                             .orElseThrow(() -> new UserNotFoundException("Email not found"));
        persistenceUser = mergeUserFields(user, persistenceUser);
        userRepository.save(persistenceUser);
        OutboxEvent event = new OutboxEvent(UPDATE, persistenceUser.getEmail());
        outboxRepository.save(event);
        return persistenceUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> group = userRepository.findAll(pageRequest);
        return group.getContent();
    }

    @Override
    @Notification(type = LOG, event = DELETE)
    @Transactional
    public void removeUser(long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
        OutboxEvent event = new OutboxEvent(DELETE, user.getEmail());
        outboxRepository.save(event);
    }

    private User mergeUserFields(User fromUser, User toUser) {
        String newName = Objects.nonNull(fromUser.getName()) ? fromUser.getName() : toUser.getName();
        int newAge = Objects.nonNull(fromUser.getAge()) ? fromUser.getAge() : toUser.getAge();
        return new User(toUser.getId(), newName, toUser.getEmail(), newAge, toUser.getCreatedAt());
    }
}