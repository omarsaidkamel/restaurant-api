package com.restaurant.controller;

import com.restaurant.dto.PaginatedResponse;
import com.restaurant.dto.User.UserCreateRequest;
import com.restaurant.dto.User.UserResponse;
import com.restaurant.dto.User.UserUpdateRequest;
import com.restaurant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "APIs for managing restaurant users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all active users")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/search")
    public PaginatedResponse<UserResponse> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return userService.searchUsers(
                search,
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/inactive")
    public List<UserResponse> getInactiveUsers() {
        return userService.getInactiveUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Integer id,
                                   @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Activate inactive user")
    @PatchMapping("/{id}/activate")
    public UserResponse activateUser(@PathVariable Integer id) {
        return userService.activateUser(id);
    }
}