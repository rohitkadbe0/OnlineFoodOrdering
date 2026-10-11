package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery,String> {

}
