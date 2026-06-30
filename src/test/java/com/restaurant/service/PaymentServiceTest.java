package com.restaurant.service;

import com.restaurant.dto.Payment.PaymentCreateRequest;
import com.restaurant.dto.Payment.PaymentResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.Payment;
import com.restaurant.mapper.PaymentMapper;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PaymentMapper paymentMapper;

    @Test
    void payOrder_shouldThrowExceptionWhenOrderNotFound() {
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(999);
        request.setPaymentMethod("cash");
        request.setDiscountType("none");
        request.setDiscountValue(BigDecimal.ZERO);

        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> paymentService.payOrder(request)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Order not found with id: 999", exception.getReason());

        verify(orderRepository).findById(999);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void payOrder_shouldThrowExceptionWhenOrderAlreadyPaid() {
        Order order = new Order();
        order.setId(1);
        order.setTotalPrice(BigDecimal.valueOf(300));
        order.setPaid(true);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1);
        request.setPaymentMethod("cash");
        request.setDiscountType("none");
        request.setDiscountValue(BigDecimal.ZERO);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> paymentService.payOrder(request)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Order is already paid", exception.getReason());

        verify(orderRepository).findById(1);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void payOrder_shouldThrowExceptionWhenPaymentAlreadyExistsForOrder() {
        Order order = new Order();
        order.setId(1);
        order.setTotalPrice(BigDecimal.valueOf(300));
        order.setPaid(false);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1);
        request.setPaymentMethod("cash");
        request.setDiscountType("none");
        request.setDiscountValue(BigDecimal.ZERO);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderId(1)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> paymentService.payOrder(request)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Payment already exists for order id: 1", exception.getReason());

        verify(orderRepository).findById(1);
        verify(paymentRepository).existsByOrderId(1);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void payOrder_shouldPaySuccessfullyWithNoDiscount() {
        Order order = new Order();
        order.setId(1);
        order.setTotalPrice(BigDecimal.valueOf(300));
        order.setPaid(false);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1);
        request.setPaymentMethod("cash");
        request.setDiscountType("none");
        request.setDiscountValue(BigDecimal.ZERO);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderId(1)).thenReturn(false);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1);
            return payment;
        });
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(mock(PaymentResponse.class));

        paymentService.payOrder(request);

        assertTrue(order.getPaid());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(order, savedPayment.getOrder());
        assertEquals("cash", savedPayment.getPaymentMethod());
        assertEquals(BigDecimal.valueOf(300), savedPayment.getOriginalAmount());
        assertEquals("none", savedPayment.getDiscountType());
        assertEquals(BigDecimal.ZERO, savedPayment.getDiscountValue());
        assertEquals(BigDecimal.valueOf(300), savedPayment.getFinalAmount());

        verify(orderRepository).save(order);
        verify(notificationService, times(3))
                .createSystemNotification(eq(order), anyString(), anyString());
    }

    @Test
    void payOrder_shouldApplyFixedDiscountSuccessfully() {
        Order order = new Order();
        order.setId(1);
        order.setTotalPrice(BigDecimal.valueOf(300));
        order.setPaid(false);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1);
        request.setPaymentMethod("card");
        request.setDiscountType("fixed");
        request.setDiscountValue(BigDecimal.valueOf(50));

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderId(1)).thenReturn(false);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1);
            return payment;
        });

        paymentService.payOrder(request);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(BigDecimal.valueOf(300), savedPayment.getOriginalAmount());
        assertEquals("fixed", savedPayment.getDiscountType());
        assertEquals(BigDecimal.valueOf(50), savedPayment.getDiscountValue());
        assertEquals(BigDecimal.valueOf(250), savedPayment.getFinalAmount());

        assertTrue(order.getPaid());

        verify(orderRepository).save(order);
        verify(notificationService, times(3))
                .createSystemNotification(eq(order), anyString(), anyString());
    }

    @Test
    void payOrder_shouldApplyPercentageDiscountSuccessfully() {
        Order order = new Order();
        order.setId(1);
        order.setTotalPrice(BigDecimal.valueOf(300));
        order.setPaid(false);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(1);
        request.setPaymentMethod("wallet");
        request.setDiscountType("percentage");
        request.setDiscountValue(BigDecimal.valueOf(10));

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderId(1)).thenReturn(false);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1);
            return payment;
        });

        paymentService.payOrder(request);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(BigDecimal.valueOf(300), savedPayment.getOriginalAmount());
        assertEquals("percentage", savedPayment.getDiscountType());
        assertEquals(BigDecimal.valueOf(10), savedPayment.getDiscountValue());
        assertEquals(0, BigDecimal.valueOf(270).compareTo(savedPayment.getFinalAmount()));
        assertTrue(order.getPaid());

        verify(orderRepository).save(order);
        verify(notificationService, times(3))
                .createSystemNotification(eq(order), anyString(), anyString());
    }
}