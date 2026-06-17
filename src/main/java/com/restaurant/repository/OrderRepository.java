package com.restaurant.repository;

import com.restaurant.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {

    Page<Order> findByPaid(Boolean paid, Pageable pageable);

    Page<Order> findByUser_Id(Integer userId, Pageable pageable);

    Page<Order> findByUser_IdAndPaid(Integer userId, Boolean paid, Pageable pageable);
}
