package ru.serggge.aston_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.serggge.aston_spring.entity.User;
import ru.serggge.aston_spring.exception.UserNotFoundException;
import ru.serggge.aston_spring.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return repository.save(user);
    }

    @Override
    public User getUser(long userId) {
        return repository.findById(userId)
                         .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public List<User> getGroup(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> group = repository.findAll(pageRequest);
        return group.getContent();
    }

    @Override
    public void removeUser(long userId) {
        repository.deleteById(userId);
    }

}
