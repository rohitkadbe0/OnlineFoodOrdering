package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.enums.PartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {


    Optional<Delivery> findById(PartnerStatus partnerStatus);
}
