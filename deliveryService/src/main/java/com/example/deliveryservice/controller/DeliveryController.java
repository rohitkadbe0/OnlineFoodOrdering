package com.example.deliveryservice.controller;

import com.example.deliveryservice.dto.OrderReadyEvent;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    @Autowired
    private DeliveryService deliveryService;
    @GetMapping("/{deliveryId}")
    public Delivery getDeliveryById(
            @PathVariable Long deliveryId) {

        return deliveryService.getDeliveryById(deliveryId);
    }

    @GetMapping("/order/{orderId}")
    public Delivery getDeliveryByOrderId(
            @PathVariable Long orderId) {

        return deliveryService.getDeliveryByOrderId(orderId);
    }

    @PutMapping("/{deliveryId}/status")
    public Delivery updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status) {

        return deliveryService.updateDeliveryStatus(
                deliveryId,
                status
        );
    }
}
