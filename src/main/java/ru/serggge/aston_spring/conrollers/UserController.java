package ru.serggge.aston_spring.conrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.serggge.aston_spring.dto.*;
import ru.serggge.aston_spring.entity.User;
import ru.serggge.aston_spring.mapper.UserMapper;
import ru.serggge.aston_spring.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateResponse create(@RequestBody @Valid CreateRequest request) {
        User user = mapper.toUser(request);
        user = service.createUser(user);
        log.info("User created: {}", user);
        return mapper.toCreateResponse(user);
    }

    @PatchMapping
    public UpdateResponse update(@RequestBody @Valid UpdateRequest request) {
        User user = mapper.toUser(request);
        user = service.updateUser(user);
        log.info("User updated: {}", user);
        return mapper.toUpdateResponse(user);
    }

    @GetMapping("/{id}")
    public ShowResponse show(@PathVariable("id") long userId) {
        User user = service.getUser(userId);
        return mapper.toShowResponse(user);
    }

    @GetMapping
    public List<ShowResponse> showGroup(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                        @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        List<User> users = service.getGroup(page, size);
        return mapper.toShowResponse(users);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long userId) {
        service.removeUser(userId);
    }
}