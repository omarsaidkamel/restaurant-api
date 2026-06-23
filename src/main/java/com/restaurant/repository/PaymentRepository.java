package com.restaurant.repository;

import com.restaurant.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    boolean existsByOrderId(Integer orderId);

    Page<Payment> findByOrder_Id(Integer orderId, Pageable pageable);

    Page<Payment> findByPaymentMethodIgnoreCase(String paymentMethod, Pageable pageable);

    Page<Payment> findByOrder_IdAndPaymentMethodIgnoreCase(
            Integer orderId,
            String paymentMethod,
            Pageable pageable
    );
}