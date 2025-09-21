package ru.serggge.aston_spring.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.serggge.aston_spring.dto.*;
import ru.serggge.aston_spring.entity.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper mapper;

    public User toUser(CreateRequest request) {
        return mapper.map(request, User.class);
    }

    public User toUser(UpdateRequest request) {
        return mapper.map(request, User.class);
    }

    public CreateResponse toCreateResponse(User user) {
        return mapper.map(user, CreateResponse.class);
    }

    public UpdateResponse toUpdateResponse(User user) {
        return mapper.map(user, UpdateResponse.class);
    }

    public ShowResponse toShowResponse(User user) {
        return mapper.map(user, ShowResponse.class);
    }

    public List<ShowResponse> toShowResponse(List<User> users) {
        return users.stream()
                    .map(this::toShowResponse)
                    .toList();
    }
}
