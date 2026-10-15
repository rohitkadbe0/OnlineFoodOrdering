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

    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Delivery> addDelivery(@RequestBody OrderReadyEvent event) {
        Delivery delivery = deliveryService.addDelivery(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(delivery);
    }

    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    @PutMapping("/{deliveryId}/accept")
    public ResponseEntity<Delivery> acceptDelivery(@RequestBody OrderReadyEvent event) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryService.addDelivery(event));
    }

    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    @PutMapping("{deliveryId}/status")
    public ResponseEntity<Delivery> updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status
    ) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(deliveryId, status));
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<Delivery> getDelivery(
            @PathVariable Long deliveryId
    ) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(deliveryId));
    }
}
