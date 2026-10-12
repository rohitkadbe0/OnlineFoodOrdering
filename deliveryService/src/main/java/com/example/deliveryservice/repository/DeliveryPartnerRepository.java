package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.DeliveryPartner;
import com.example.deliveryservice.enums.PartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner,Long> {

    Optional<DeliveryPartner> findPartnerStatus(PartnerStatus partnerStatus);
}
