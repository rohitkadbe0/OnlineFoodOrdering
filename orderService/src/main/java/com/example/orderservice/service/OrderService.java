package com.example.orderservice.service;

import com.example.orderservice.client.FoodClient;
import com.example.orderservice.dto.FoodResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FoodClient foodClient;

    public Order saveOrder(Order order) {
        FoodResponse foodResponse = foodClient.getFoodById(order.getFoodId());
        if (foodResponse == null) {
            throw new RuntimeException("Food not found");
        }
        if (!Boolean.TRUE.equals(foodResponse.getAvailable())) {
            throw new RuntimeException("Food is not available");
        }
        order.setFoodName(foodResponse.getFoodName());
        order.setPrice(foodResponse.getPrice());
        BigDecimal totalAmount = foodResponse.getPrice().multiply(
                BigDecimal.valueOf(order.getQuantity())
        );
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public List<Order> findCustomerId(Long customerId, LocalDate fromDate, LocalDate toDate) {

        if (fromDate == null && toDate == null) {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
            return orderRepository.findByCustomerId(customerId, startOfDay, endOfDay);
        }
        LocalDateTime startDateTime = fromDate.atStartOfDay();

        LocalDateTime endDateTime =
                toDate.plusDays(1)
                        .atStartOfDay()
                        .minusNanos(1);
        return orderRepository.findByCustomerId(customerId, startDateTime, endDateTime);
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
public Order updateOrderStatus(Long orderId,OrderStatus status){
        Optional<Order>optionalOrder=orderRepository.findByOrderId(orderId);
        if(optionalOrder.isEmpty()){
            throw new IllegalArgumentException("Order not found");
        }
        Order order =optionalOrder.get();
        order.setOrderStatus(status);
        return orderRepository.save(order);
}
}
