package com.restaurant.service;

import com.restaurant.dto.User.UserCreateRequest;
import com.restaurant.dto.User.UserResponse;
import com.restaurant.entity.User;
import com.restaurant.mapper.UserMapper;
import com.restaurant.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Ahmed");
        request.setEmail("ahmed@test.com");
        request.setLoyaltyPoints(0);

        User user = new User();
        user.setName("Ahmed");
        user.setEmail("ahmed@test.com");
        user.setLoyaltyPoints(0);
        user.setActive(true);

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setName("Ahmed");
        savedUser.setEmail("ahmed@test.com");
        savedUser.setLoyaltyPoints(0);
        savedUser.setActive(true);

        UserResponse response = new UserResponse();
        response.setId(1);
        response.setName("Ahmed");
        response.setEmail("ahmed@test.com");
        response.setLoyaltyPoints(0);
        response.setActive(true);

        when(userRepository.existsByEmail("ahmed@test.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Ahmed", result.getName());
        assertEquals("ahmed@test.com", result.getEmail());
        assertEquals(0, result.getLoyaltyPoints());
        assertTrue(result.getActive(),"");

        verify(userRepository).existsByEmail("ahmed@test.com");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Ahmed");
        request.setEmail("ahmed@test.com");
        request.setLoyaltyPoints(0);

        when(userRepository.existsByEmail("ahmed@test.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.createUser(request)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("User email already exists: ahmed@test.com", exception.getReason());
        verify(userRepository).existsByEmail("ahmed@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUserById(999)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("User not found with id: 999", exception.getReason());
    }

    @Test
    void deleteUser_shouldSetActiveFalse() {
        User user = new User();
        user.setId(2);
        user.setName("Mohamed");
        user.setEmail("mohamed@test.com");
        user.setActive(true);

        when(userRepository.findById(2)).thenReturn(Optional.of(user));

        userService.deleteUser(2);

        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_shouldSetActiveTrue() {
        User user = new User();
        user.setId(2);
        user.setName("Mohamed");
        user.setEmail("mohamed@test.com");
        user.setLoyaltyPoints(10);
        user.setActive(false);

        User savedUser = new User();
        savedUser.setId(2);
        savedUser.setName("Mohamed");
        savedUser.setEmail("mohamed@test.com");
        savedUser.setLoyaltyPoints(10);
        savedUser.setActive(true);

        UserResponse response = new UserResponse();
        response.setId(2);
        response.setName("Mohamed");
        response.setEmail("mohamed@test.com");
        response.setLoyaltyPoints(10);
        response.setActive(true);

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.activateUser(2);

        assertTrue(user.getActive());
        assertEquals(2, result.getId());
        assertEquals("Mohamed", result.getName());
        assertTrue(result.getActive());

        verify(userRepository).save(user);
    }
}