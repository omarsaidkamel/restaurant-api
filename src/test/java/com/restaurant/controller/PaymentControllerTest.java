package com.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.dto.Payment.PaymentCreateRequest;
import com.restaurant.dto.Payment.PaymentResponse;
import com.restaurant.dto.PaginatedResponse;
import com.restaurant.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void getAllPayments_shouldReturnOk() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of());

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk());
    }

    @Test
    void payOrder_shouldReturnCreated() throws Exception {
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1);
        request.setPaymentMethod("cash");
        request.setDiscountType("none");
        request.setDiscountValue(BigDecimal.ZERO);

        when(paymentService.payOrder(any(PaymentCreateRequest.class)))
                .thenReturn(mock(PaymentResponse.class));

        mockMvc.perform(post("/api/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void searchPayments_shouldReturnOk() throws Exception {
        PaginatedResponse<PaymentResponse> response =
                new PaginatedResponse<>(
                        List.of(),
                        0,
                        5,
                        0,
                        0,
                        true
                );

        when(paymentService.searchPayments(
                eq(null),
                eq(null),
                eq(0),
                eq(5),
                eq("id"),
                eq("asc")
        )).thenReturn(response);

        mockMvc.perform(get("/api/payments/search")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk());
    }
}