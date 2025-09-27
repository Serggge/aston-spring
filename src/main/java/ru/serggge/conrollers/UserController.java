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
    public CreateUserResponseDto create(CreateUserRequestDto requestDto) {
        User user = mapper.toEntity(requestDto);
        user = service.createUser(user);
        return mapper.toCreateResponse(user);
    }

    @Override
    public UpdateUserResponseDto update(UpdateUserRequestDto requestDto) {
        User user = mapper.toEntity(requestDto);
        user = service.updateUser(user);
        return mapper.toUpdateResponse(user);
    }

    @Override
    public FindUserResponseDto findById(long userId) {
        User user = service.getUser(userId);
        return mapper.toShowResponse(user);
    }

    @Override
    public List<FindUserResponseDto> findAll(int page, int size) {
        List<User> users = service.getGroup(page, size);
        return mapper.toShowResponse(users);
    }

    @Override
    public void delete(long userId) {
        service.removeUser(userId);
    }
}