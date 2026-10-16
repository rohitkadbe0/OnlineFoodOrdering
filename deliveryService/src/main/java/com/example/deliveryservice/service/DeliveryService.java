package com.example.deliveryservice.service;

import com.example.deliveryservice.client.UserClient;
import com.example.deliveryservice.dto.OrderReadyEvent;
import com.example.deliveryservice.dto.UserResponse;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.entity.DeliveryPartner;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.repository.DeliveryPartnerRepository;
import com.example.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;
    @Autowired
    private UserClient userClient;


    // Called automatically when Kafka receives READY_FOR_PICKUP
    @Transactional
    public Delivery addDelivery(OrderReadyEvent event) {

        if (event == null
                || event.getOrderId() == null
                || event.getRestaurantId() == null
                || !"READY_FOR_PICKUP".equalsIgnoreCase(event.getStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order-ready event"
            );
        }

        // Avoid creating duplicate deliveries for the same order
        var existingDelivery =
                deliveryRepository.findByOrderId(event.getOrderId());

        if (existingDelivery.isPresent()) {
            return existingDelivery.get();
        }

        // Find an available delivery partner
        DeliveryPartner partner =
                deliveryPartnerRepository.findFirstByAvailableTrue()
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "No available delivery partner found"
                        ));

        // Validate partner role
        UserResponse userResponse =
                userClient.getUserById(partner.getUserId());

        if (userResponse == null
                || userResponse.getRoles() == null
                || (!userResponse.getRoles().contains("DELIVERY_PARTNER")
                && !userResponse.getRoles().contains("ROLE_DELIVERY_PARTNER"))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The selected user is not a delivery partner"
            );
        }

        // Create delivery
        Delivery delivery = new Delivery();

        delivery.setOrderId(event.getOrderId());
        delivery.setCustomerId(event.getCustomerId());
        delivery.setRestaurantId(event.getRestaurantId());
        delivery.setDeliveryPartnerId(partner.getDeliveryPartnerId());
        delivery.setPickupAddress(event.getPickUpAddress());
        delivery.setDeliveryAddress(event.getDeliveryAddress());

        // Automatically start at ACCEPTED
        delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
        delivery.setAssignedAt(LocalDateTime.now());

        // Mark partner unavailable
        partner.setAvailable(false);
        deliveryPartnerRepository.save(partner);

        return deliveryRepository.save(delivery);
    }

    // Update delivery status
    @Transactional
    public Delivery updateDeliveryStatus(
            Long deliveryId,
            DeliveryStatus newStatus) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Delivery not found"
                ));

        if (newStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        DeliveryStatus current = delivery.getDeliveryStatus();

        // Do not allow updates after a final status
        if (current == DeliveryStatus.DELIVERED
                || current == DeliveryStatus.FAILED
                || current == DeliveryStatus.CANCELLED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Delivery is already in a final state"
            );
        }

        // Validate status transitions
        boolean valid =
                (current == DeliveryStatus.ACCEPTED
                        && newStatus == DeliveryStatus.PICKED_UP)
                        || (current == DeliveryStatus.PICKED_UP
                        && newStatus == DeliveryStatus.OUT_FOR_DELIVERY)
                        || (current == DeliveryStatus.OUT_FOR_DELIVERY
                        && newStatus == DeliveryStatus.REACHED_LOCATION)
                        || (current == DeliveryStatus.REACHED_LOCATION
                        && newStatus == DeliveryStatus.DELIVERED)
                        || newStatus == DeliveryStatus.FAILED
                        || newStatus == DeliveryStatus.CANCELLED;

        if (!valid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition: "
                            + current + " to " + newStatus
            );
        }

        if (newStatus == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        }

        if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }

        // Release partner after delivery ends
        if (newStatus == DeliveryStatus.DELIVERED
                || newStatus == DeliveryStatus.FAILED
                || newStatus == DeliveryStatus.CANCELLED) {

            DeliveryPartner partner =
                    deliveryPartnerRepository
                            .findById(delivery.getDeliveryPartnerId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Delivery partner not found"
                            ));

            partner.setAvailable(true);
            deliveryPartnerRepository.save(partner);
        }

        delivery.setDeliveryStatus(newStatus);

        return deliveryRepository.save(delivery);
    }

    // Get delivery by ID
    @Transactional(readOnly = true)
    public Delivery getDeliveryById(Long deliveryId) {

        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Delivery not found"
                ));
    }
}