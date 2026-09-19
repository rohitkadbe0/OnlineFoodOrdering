package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public void saveEntry(Order order) {
        orderRepository.save(order);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public List<Order> findCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> findRestaurantId(Long restaurantId, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null && toDate == null) {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
            return orderRepository.findByRestaurantId(restaurantId, startOfDay, endOfDay);
        }
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay().minusNanos(1);
        return orderRepository.findByRestaurantId(
                restaurantId, startDateTime, endDateTime);

    }

    public List<Order> findRestaurantName(String restaurant) {
        return orderRepository.findByRestaurantName(restaurant);
    }
}
