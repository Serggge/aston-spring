package ru.serggge.conrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.serggge.dto.*;
import ru.serggge.entity.User;
import ru.serggge.mapper.UserMapper;
import ru.serggge.service.UserService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserOperations {

    private final UserService service;
    private final UserMapper mapper;

    @Override
    public CreateResponse create(CreateRequest request) {
        User user = mapper.toEntity(request);
        user = service.createUser(user);
        return mapper.toCreateResponse(user);
    }

    @Override
    public UpdateResponse update(UpdateRequest request) {
        User user = mapper.toEntity(request);
        user = service.updateUser(user);
        return mapper.toUpdateResponse(user);
    }

    @Override
    public ShowResponse show(long userId) {
        User user = service.getUser(userId);
        return mapper.toShowResponse(user);
    }

    @Override
    public List<ShowResponse> showGroup(int page, int size) {
        List<User> users = service.getGroup(page, size);
        return mapper.toShowResponse(users);
    }

    @Override
    public void delete(long userId) {
        service.removeUser(userId);
    }
}