package ru.serggge.conrollers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import ru.serggge.exception.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.serggge.dto.*;
import ru.serggge.model.PojoAccountEvent;

import java.util.List;

@Tag(name = "User", description = "The User API")
@RequestMapping("/users")
public interface UserOperations {


    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserResponseDto.class))
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateUserResponseDto create(@RequestBody @Valid CreateUserRequestDto requestDto);

    @Operation(summary = "Update user fields")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateUserResponseDto.class))

                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
    })
    @PatchMapping
    UpdateUserResponseDto update(@RequestBody @Valid UpdateUserRequestDto requestDto);

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the users",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FindUserResponseDto.class))
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @Parameters(value = {
            @Parameter(name = "id",
                    in = ParameterIn.PATH,
                    description = "User ID",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"))
    })
    @GetMapping("/{id}")
    FindUserResponseDto findById(@PathVariable("id") long userId);


    @Operation(summary = "Gets users pageable")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the users",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FindUserResponseDto.class)))
                    })
    })
    @GetMapping
    List<FindUserResponseDto> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                      @RequestParam(value = "size", required = false, defaultValue = "10") int size);

    @Operation(summary = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @Parameters(value = {
            @Parameter(name = "id",
                    in = ParameterIn.PATH,
                    description = "User ID",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") long userId);
}