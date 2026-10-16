package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner,Long> {
Optional<DeliveryPartner> findFirstByAvailableTrue();
Optional<DeliveryPartner> findByUserId(Long userId);
boolean existsByUserId(Long userId);
}
