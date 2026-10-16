package com.example.deliveryservice.controller;

import com.example.deliveryservice.entity.DeliveryPartner;
import com.example.deliveryservice.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/delivery-partners")
public class DeliveryPartnerController {
    @Autowired
    private  DeliveryPartnerService deliveryPartnerService;
    @PreAuthorize("hasAnyRole('DELIVERY_PARTNER', 'ADMIN')")

    @PostMapping
    public ResponseEntity<DeliveryPartner> registerPartner(
            @RequestBody DeliveryPartner partner) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryPartnerService.registerPartner(partner));
    }
}
