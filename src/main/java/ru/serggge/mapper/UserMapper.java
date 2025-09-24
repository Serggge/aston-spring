package ru.serggge.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.serggge.dto.*;
import ru.serggge.entity.User;
import java.util.List;

@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper mapper;

    public User toEntity(CreateRequest request) {
        return mapper.map(request, User.class);
    }

    public User toEntity(UpdateRequest request) {
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
