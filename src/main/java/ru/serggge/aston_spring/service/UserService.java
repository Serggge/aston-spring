package ru.serggge.aston_spring.service;

import ru.serggge.aston_spring.entity.User;
import java.util.List;

public interface UserService {

    public User createUser(User user);

    public User updateUser(User user);

    public User getUser(long userId);

    public List<User> getGroup(int page, int size);

    public void removeUser(long userId);
}
