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

    @PostMapping
    public ResponseEntity<Order> add(@RequestBody Order order) {
        Order savedOrder = orderService.saveOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/orderId/{orderId}")
    public ResponseEntity<Order> findByOrderId(@PathVariable Long orderId) {
        Optional<Order> orders = orderService.findOrderId(orderId);
        if (orders.isPresent()) {
            return ResponseEntity.ok(orders.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customerId/{customerId}")
    public ResponseEntity<?> findByCustomerId(
            @PathVariable Long customerId, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        List<Order> order =
                orderService.findCustomerId(customerId, from, to);
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);

    }

    @GetMapping("/restaurantId/{restaurantId}")
    public ResponseEntity<?> findRestaurantId(
            @PathVariable Long restaurantId, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        List<Order> orders = orderService.findRestaurantId(restaurantId, from, to);
        if (orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }


}
