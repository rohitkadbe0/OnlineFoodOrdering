package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping
    public ResponseEntity<Order> add(@RequestBody Order order){
        orderService.saveEntry(order);
        return ResponseEntity.ok(order);
    }
    @GetMapping
    public ResponseEntity<List<Order>> getAll(){
        return ResponseEntity.ok(orderService.getAll());
    }
    @GetMapping("/orderId/{orderId}")
    public ResponseEntity<Order> findByOrderId(@PathVariable Long orderId){
        Optional<Order> orders=orderService.findOrderId(orderId);
        if (orders.isPresent()){
            return ResponseEntity.ok(orders.get());
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/customerId/{customerId}")
    public ResponseEntity<?> findByCustomerId(
            @PathVariable Long customerId){
        List<Order> order=
                orderService.findCustomerId(customerId);
            return ResponseEntity.ok(order.get());

    }


}
