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
    // Update delivery progress
    @PutMapping("/{deliveryId}/status/{status}")
    @PreAuthorize("hasAnyRole('DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<Delivery> updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status) {

        return ResponseEntity.ok(
                deliveryService.updateDeliveryStatus(deliveryId, status)
        );
    }

    // Get delivery details
    @GetMapping("/{deliveryId}")
    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<Delivery> getDelivery(
            @PathVariable Long deliveryId) {

        return ResponseEntity.ok(
                deliveryService.getDeliveryById(deliveryId)
        );
    }
}
