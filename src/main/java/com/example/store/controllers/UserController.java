package com.example.store.controllers;

import com.example.store.dtos.ChangePasswordRequest;
import com.example.store.dtos.RegisterUserRequest;
import com.example.store.dtos.UpdateUserRequest;
import com.example.store.dtos.UserDto;
import com.example.store.mappers.UserMapper;
import com.example.store.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //! Get all users
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system, sorted by the specified field.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved the list of users",
                            content = @Content(mediaType = "application/json", schema =@Schema(implementation = UserDto.class))
                    )
            }
    )
    @GetMapping
    public Iterable<UserDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy
    ) {
        if (!Set.of("name", "email").contains(sortBy))
            sortBy = "name";

        return userRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    //! Get user by ID
    @Operation(summary = "Get user by ID", description = "Returns a user by their ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved the user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "User not found"
                    )
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    //! Create a new user
    @Operation(summary = "Create a new user", description = "Creates a new user in the system.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "User created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(
            @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var user = userMapper.toEntity(request);
        userRepository.save(user);

        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto );
    }

    //! Update an existing user
    @Operation(summary = "Update an existing user", description = "Updates the details of an existing user by ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "User updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "User not found"
                    )
            }
    )
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "userId") Long id,
            @RequestBody UpdateUserRequest request
    ) {
        var user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        userMapper.update(request, user);
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    //! Delete a user
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "User deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "User not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    //! Change user password
    @Operation(summary = "Change user password", description = "Changes the password of a user by their ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "Password changed successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "User not found"
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized - old password does not match"
                    )
            }
    )
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request
    ) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }
}