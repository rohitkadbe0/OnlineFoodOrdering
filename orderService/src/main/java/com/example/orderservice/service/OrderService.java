package com.example.orderservice.service;

import com.example.orderservice.client.FoodClient;
import com.example.orderservice.dto.FoodResponse;
import com.example.orderservice.dto.OrderReadyEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private OrderEventProducer orderEventProducer;

    @Transactional
    public Order markReadyForPickup(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found: " + orderId
                ));

        if (order.getOrderStatus() != OrderStatus.READY_FOR_PICKUP) {

            if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Order must be CONFIRMED or READY_FOR_PICKUP"
                );
            }

            order.setOrderStatus(OrderStatus.READY_FOR_PICKUP);
            order = orderRepository.save(order);
        }

        // Publish event for both newly ready and already-ready orders.
        OrderReadyEvent event = new OrderReadyEvent(
                order.getOrderId(),
                order.getCustomerId(),
                order.getRestaurantId(),
                order.getPickUpAddress(),
                order.getDeliveryAddress(),
                "READY_FOR_PICKUP"
        );

        orderEventProducer.publishOrderReady(event);

        return order;
    }


}
