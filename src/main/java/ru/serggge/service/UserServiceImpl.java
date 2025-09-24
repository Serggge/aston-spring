package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.serggge.entity.User;
import ru.serggge.exception.UserNotFoundException;
import ru.serggge.repository.UserRepository;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional
    public User createUser(User user) {
        repository.findByEmail(user.getEmail())
                  .ifPresent(u -> {
                      throw new IllegalArgumentException("Email already exists");
                  });
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User persistenceUser = repository.findByEmail(user.getEmail())
                                         .orElseThrow(() -> new UserNotFoundException("Email not found"));
        persistenceUser = mergeUserFields(user, persistenceUser);
        return repository.save(persistenceUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(long userId) {
        return repository.findById(userId)
                         .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getGroup(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> group = repository.findAll(pageRequest);
        return group.getContent();
    }

    @Override
    @Transactional
    public void removeUser(long userId) {
        User user = repository.findById(userId)
                              .orElseThrow(() -> new UserNotFoundException("User not found"));
        repository.delete(user);
    }

    private User mergeUserFields(User fromUser, User toUser) {
        String newName = Objects.nonNull(fromUser.getName()) ? fromUser.getName() : toUser.getName();
        int newAge = Objects.nonNull(fromUser.getAge()) ? fromUser.getAge() : toUser.getAge();
        return new User(toUser.getId(), newName, toUser.getEmail(), newAge, toUser.getCreatedAt());
    }
}