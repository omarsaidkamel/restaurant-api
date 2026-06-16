package com.restaurant.repository;

import com.restaurant.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    boolean existsByOrderId(Integer orderId);
}