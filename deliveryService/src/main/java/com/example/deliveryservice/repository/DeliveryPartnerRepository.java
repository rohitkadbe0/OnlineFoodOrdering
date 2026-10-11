package com.example.deliveryservice.repository;

import com.example.deliveryservice.enums.PartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<PartnerStatus,Long> {

    Optional<PartnerStatus> findPartnerStatus(PartnerStatus partnerStatus);
}
