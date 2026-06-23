package com.restaurant.service;

import com.restaurant.dto.PaginatedResponse;
import com.restaurant.dto.User.UserCreateRequest;
import com.restaurant.dto.User.UserResponse;
import com.restaurant.dto.User.UserUpdateRequest;
import com.restaurant.entity.User;
import com.restaurant.mapper.UserMapper;
import com.restaurant.repository.UserRepository;
import com.restaurant.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "name", "email", "loyaltyPoints");
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse getUserById(Integer id) {
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User email already exists: " + request.getEmail()
            );
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse updateUser(Integer id, UserUpdateRequest request) {
        User existingUser = findUserById(id);

        userRepository.findByEmail(request.getEmail())
                .ifPresent(userWithSameEmail -> {
                    if (!userWithSameEmail.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "User email already exists: " + request.getEmail()
                        );
                    }
                });

        userMapper.updateEntity(existingUser, request);

        User savedUser = userRepository.save(existingUser);

        return userMapper.toResponse(savedUser);
    }

    public void deleteUser(Integer id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id
                ));
    }

    public PaginatedResponse<UserResponse> searchUsers(
            String search,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        PaginationUtils.validatePageAndSize(page, size);

        Sort sort = PaginationUtils.buildSort(
                sortBy,
                direction,
                ALLOWED_SORT_FIELDS
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        String normalizedSearch = normalize(search);

        Page<User> userPage;

        if (normalizedSearch != null) {
            userPage = userRepository
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            normalizedSearch,
                            normalizedSearch,
                            pageable
                    );
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponse> content = userPage.getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}