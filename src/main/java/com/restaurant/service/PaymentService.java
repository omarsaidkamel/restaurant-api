package com.restaurant.service;

import com.restaurant.dto.Payment.PaymentCreateRequest;
import com.restaurant.dto.Payment.PaymentResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.Payment;
import com.restaurant.mapper.PaymentMapper;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMapper = paymentMapper;
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    public PaymentResponse getPaymentById(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found with id: " + id
                ));

        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse payOrder(PaymentCreateRequest request) {

        if (paymentRepository.existsById(request.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment already exists with id: " + request.getId()
            );
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + request.getOrderId()
                ));

        if (Boolean.TRUE.equals(order.getPaid())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order is already paid"
            );
        }

        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment already exists for order id: " + order.getId()
            );
        }

        BigDecimal originalAmount = order.getTotalPrice();
        BigDecimal finalAmount = calculateFinalAmount(
                originalAmount,
                request.getDiscountType(),
                request.getDiscountValue()
        );

        Payment payment = new Payment();
        payment.setId(request.getId());
        payment.setOrder(order);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setOriginalAmount(originalAmount);
        payment.setDiscountType(request.getDiscountType());
        payment.setDiscountValue(request.getDiscountValue());
        payment.setFinalAmount(finalAmount);
        payment.setPaidAt(LocalDateTime.now());

        order.setPaid(true);
        orderRepository.save(order);

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    private BigDecimal calculateFinalAmount(BigDecimal originalAmount,
                                            String discountType,
                                            BigDecimal discountValue) {

        if (discountType.equalsIgnoreCase("none")) {
            return originalAmount;
        }

        if (discountType.equalsIgnoreCase("fixed")) {
            if (discountValue.compareTo(originalAmount) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Fixed discount cannot be greater than original amount"
                );
            }

            return originalAmount.subtract(discountValue);
        }

        if (discountType.equalsIgnoreCase("percentage")) {
            if (discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Percentage discount cannot exceed 100"
                );
            }

            BigDecimal discountAmount = originalAmount
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100));

            return originalAmount.subtract(discountAmount);
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid discount type: " + discountType
        );
    }
}