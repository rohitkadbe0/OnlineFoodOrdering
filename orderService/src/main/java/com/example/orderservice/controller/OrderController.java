package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    // Mark an order as READY_FOR_PICKUP and publish the Kafka event
    @PutMapping("/{orderId}/ready-for-pickup")
    public ResponseEntity<Order> markReadyForPickup(
            @PathVariable Long orderId) {

        Order updatedOrder =
                orderService.markReadyForPickup(orderId);

        return ResponseEntity.ok(updatedOrder);
    }
}
