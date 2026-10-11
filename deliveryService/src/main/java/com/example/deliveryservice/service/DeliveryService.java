package com.example.deliveryservice.service;

import com.example.deliveryservice.client.UserClient;
import com.example.deliveryservice.dto.OrderReadyEvent;
import com.example.deliveryservice.dto.UserResponse;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.enums.PartnerStatus;
import com.example.deliveryservice.repository.DeliveryPartnerRepository;
import com.example.deliveryservice.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DeliveryService {
    @Autowired
private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;
    @Autowired
    private UserClient userClient;


    @Transactional
    public Delivery addDelivery(OrderReadyEvent orderReadyEvent){
        // Process only ready orders
        if(orderReadyEvent.getOrderId()==null || !"READY_FOR_PICKUP".equalsIgnoreCase(
                orderReadyEvent.getStatus())){
            throw new IllegalArgumentException("Invalid order ");
        }
        // Prevent duplicate deliveries for the same order
        if(deliveryRepository.existsById(orderReadyEvent.getOrderId())){
return deliveryRepository.findById(orderReadyEvent.getOrderId()).orElseThrow();
        }
        //finding an active and available partner
        DeliveryStatus deliveryStatus=deliveryPartnerRepository.findPartnerStatus(
                PartnerStatus.ACTIVE)
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "No delivery partner is available near your location"
                ));

        UserResponse userResponse=userClient.getUserById(
                p
        )
    }

}
