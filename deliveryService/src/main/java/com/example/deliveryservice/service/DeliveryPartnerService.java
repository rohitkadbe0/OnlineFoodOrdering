package com.example.deliveryservice.service;

import com.example.deliveryservice.entity.DeliveryPartner;
import com.example.deliveryservice.repository.DeliveryPartnerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class DeliveryPartnerService {
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    public DeliveryPartnerService(
            DeliveryPartnerRepository deliveryPartnerRepository) {
        this.deliveryPartnerRepository = deliveryPartnerRepository;
    }

    public DeliveryPartner registerPartner(DeliveryPartner partner) {
        if (partner.getUserId() == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (deliveryPartnerRepository.existsByUserId(partner.getUserId())) {
            throw new IllegalArgumentException(
                    "A delivery partner already exists for this userId");
        }

        partner.setDeliveryPartnerId(null);
        partner.setAvailable(true);
        partner.setCreatedAt(LocalDateTime.now());

        return deliveryPartnerRepository.save(partner);
    }
}
