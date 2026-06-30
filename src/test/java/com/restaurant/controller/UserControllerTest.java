package com.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.dto.User.UserCreateRequest;
import com.restaurant.dto.User.UserResponse;
import com.restaurant.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1);
        response.setName("Ahmed");
        response.setEmail("ahmed@test.com");
        response.setLoyaltyPoints(0);
        response.setActive(true);

        when(userService.getAllUsers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Ahmed");
        request.setEmail("ahmed@test.com");
        request.setLoyaltyPoints(0);

        UserResponse response = new UserResponse();
        response.setId(1);
        response.setName("Ahmed");
        response.setEmail("ahmed@test.com");
        response.setLoyaltyPoints(0);
        response.setActive(true);

        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateUser_shouldReturnOk() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1);
        response.setName("Ahmed");
        response.setEmail("ahmed@test.com");
        response.setLoyaltyPoints(0);
        response.setActive(true);

        when(userService.activateUser(1)).thenReturn(response);

        mockMvc.perform(patch("/api/users/1/activate"))
                .andExpect(status().isOk());
    }
}