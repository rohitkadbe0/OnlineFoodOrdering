package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(Long orderId);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByRestaurantName(String restaurantName);

    List<Order> findByRestaurantId(
            Long restaurantId, LocalDateTime fromDate, LocalDateTime toDate
    );
}
