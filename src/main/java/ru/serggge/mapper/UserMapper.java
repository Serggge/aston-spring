package ru.serggge.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.serggge.dto.*;
import ru.serggge.entity.User;
import java.util.List;

@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper mapper;

    public User toEntity(CreateUserRequestDto request) {
        return mapper.map(request, User.class);
    }

    public User toEntity(UpdateUserRequestDto request) {
        return mapper.map(request, User.class);
    }

    public CreateUserResponseDto toCreateResponse(User user) {
        return mapper.map(user, CreateUserResponseDto.class);
    }

    public UpdateUserResponseDto toUpdateResponse(User user) {
        return mapper.map(user, UpdateUserResponseDto.class);
    }

    public FindUserResponseDto toShowResponse(User user) {
        return mapper.map(user, FindUserResponseDto.class);
    }

    public List<FindUserResponseDto> toShowResponse(List<User> users) {
        return users.stream()
                    .map(this::toShowResponse)
                    .toList();
    }
}
