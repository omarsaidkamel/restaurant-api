package com.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.dto.Notification.NotificationCreateRequest;
import com.restaurant.dto.Notification.NotificationResponse;
import com.restaurant.dto.PaginatedResponse;
import com.restaurant.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void getAllNotifications_shouldReturnOk() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void createNotification_shouldReturnCreated() throws Exception {
        NotificationCreateRequest request = new NotificationCreateRequest();
        request.setOrderId(1);
        request.setNotificationType("email");
        request.setMessage("Order paid successfully");

        when(notificationService.createNotification(any(NotificationCreateRequest.class)))
                .thenReturn(mock(NotificationResponse.class));

        mockMvc.perform(post("/api/notifications")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getNotificationsByOrderId_shouldReturnOk() throws Exception {
        when(notificationService.getNotificationsByOrderId(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/order/1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchNotifications_shouldReturnOk() throws Exception {
        PaginatedResponse<NotificationResponse> response =
                new PaginatedResponse<>(
                        List.of(),
                        0,
                        5,
                        0,
                        0,
                        true
                );

        when(notificationService.searchNotifications(
                eq(null),
                eq(null),
                eq(0),
                eq(5),
                eq("id"),
                eq("asc")
        )).thenReturn(response);

        mockMvc.perform(get("/api/notifications/search")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk());
    }
}