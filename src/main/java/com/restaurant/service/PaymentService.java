package com.restaurant.service;

import com.restaurant.dto.PaginatedResponse;
import com.restaurant.dto.Payment.PaymentCreateRequest;
import com.restaurant.dto.Payment.PaymentResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.Payment;
import com.restaurant.mapper.PaymentMapper;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import com.restaurant.util.BusinessValidationUtils;
import com.restaurant.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PaymentService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "paymentMethod", "originalAmount", "discountType",
                    "discountValue", "finalAmount", "paidAt");
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          PaymentMapper paymentMapper, NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMapper = paymentMapper;
        this.notificationService = notificationService;
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

        BusinessValidationUtils.validatePaymentMethod(request.getPaymentMethod());
        BusinessValidationUtils.validateDiscountType(request.getDiscountType());

        String paymentMethod = BusinessValidationUtils.normalize(request.getPaymentMethod());
        String discountType = BusinessValidationUtils.normalize(request.getDiscountType());

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
                discountType,
                request.getDiscountValue()
        );

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setOriginalAmount(originalAmount);
        payment.setDiscountType(discountType);
        payment.setDiscountValue(request.getDiscountValue());
        payment.setFinalAmount(finalAmount);
        payment.setPaidAt(LocalDateTime.now());

        order.setPaid(true);
        orderRepository.save(order);

        Payment savedPayment = paymentRepository.save(payment);
        notificationService.createSystemNotification(
                order,
                "email",
                "Order paid successfully"
        );

        notificationService.createSystemNotification(
                order,
                "sms",
                "Order paid successfully"
        );

        notificationService.createSystemNotification(
                order,
                "app",
                "Order paid successfully"
        );
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

    public PaginatedResponse<PaymentResponse> searchPayments(
            Integer orderId,
            String paymentMethod,
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

        String normalizedPaymentMethod = normalize(paymentMethod);

        Page<Payment> paymentPage;

        if (orderId != null && normalizedPaymentMethod != null) {
            paymentPage = paymentRepository.findByOrder_IdAndPaymentMethodIgnoreCase(
                    orderId,
                    normalizedPaymentMethod,
                    pageable
            );
        } else if (orderId != null) {
            paymentPage = paymentRepository.findByOrder_Id(orderId, pageable);
        } else if (normalizedPaymentMethod != null) {
            paymentPage = paymentRepository.findByPaymentMethodIgnoreCase(
                    normalizedPaymentMethod,
                    pageable
            );
        } else {
            paymentPage = paymentRepository.findAll(pageable);
        }

        List<PaymentResponse> content = paymentPage.getContent()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                paymentPage.getNumber(),
                paymentPage.getSize(),
                paymentPage.getTotalElements(),
                paymentPage.getTotalPages(),
                paymentPage.isLast()
        );
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}