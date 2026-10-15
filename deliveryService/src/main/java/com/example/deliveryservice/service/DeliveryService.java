package com.example.deliveryservice.service;

import com.example.deliveryservice.client.UserClient;
import com.example.deliveryservice.dto.OrderReadyEvent;
import com.example.deliveryservice.dto.UserResponse;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.entity.DeliveryPartner;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.enums.PartnerStatus;
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


    @Transactional
    public Delivery addDelivery(OrderReadyEvent orderReadyEvent) {
        // Validate the order event
        if (orderReadyEvent == null || orderReadyEvent.getOrderId() == null || !"READY_FOR_PICKUP".equalsIgnoreCase(
                orderReadyEvent.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order event"
            );
        }
        // Prevent duplicate deliveries for the same order
        if (deliveryRepository.existsByOrderId(orderReadyEvent.getOrderId())) {
            return deliveryRepository.findById(orderReadyEvent.getOrderId()).orElseThrow();
        }
        //finding an active and available partner
        DeliveryPartner partner = deliveryPartnerRepository.  findFirstByPartnerStatusAndAvailableTrue(
                PartnerStatus.ACTIVE
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No delivery boy is available"));

        // Fetch the partner's account from UserService
        UserResponse userResponse = userClient.getUserById(partner.getUserId());
        if (userResponse == null || userResponse.getRoles() == null || !(userResponse.getRoles().contains("DELIVERY_PARTNER") ||
                userResponse.getRoles().contains("ROLE_DELIVERY_PARTNER"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected user is not a delivery partner");
        }
        // Create the delivery
        Delivery delivery1 = new Delivery();
        delivery1.setOrderId(orderReadyEvent.getOrderId());
        delivery1.setDeliveryPartnerId(partner.getDeliveryPartnerId());
        delivery1.setCustomerId(orderReadyEvent.getCustomerId());
        delivery1.setRestaurantId(orderReadyEvent.getRestaurantId());
        delivery1.setPickupAddress(orderReadyEvent.getPickUpAddress());
        delivery1.setDeliveryAddress(orderReadyEvent.getDeliveryAddress());
        delivery1.setDeliveryStatus(DeliveryStatus.ASSIGNED);
        delivery1.setAssignedAt(LocalDateTime.now());

        //Mark the partner unavailable
        partner.setAvailable(false);
        deliveryPartnerRepository.save(partner);

        return deliveryRepository.save(delivery1);
    }

    @Transactional
    public Delivery updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Delivery not Found"
                ));
        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Status is required"
            );
        }
        DeliveryStatus current = delivery.getDeliveryStatus();
        if (current == DeliveryStatus.DELIVERED || current == DeliveryStatus.FAILED || current == DeliveryStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Delivery is already in a final state");
        }
        boolean valid = (current == DeliveryStatus.ASSIGNED && status == DeliveryStatus.PICKED_UP) || (current == DeliveryStatus.PICKED_UP
                && status == DeliveryStatus.OUT_FOR_DELIVERY)
                || (current == DeliveryStatus.OUT_FOR_DELIVERY
                && status == DeliveryStatus.REACHED_LOCATION)
                || (current == DeliveryStatus.REACHED_LOCATION
                && status == DeliveryStatus.DELIVERED)
                || status == DeliveryStatus.FAILED
                || status == DeliveryStatus.CANCELLED;

        if (!valid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition: " + current + " to " + status);
        }
        if (status == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        }
        if (status == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }
        if (status == DeliveryStatus.DELIVERED ||
                status == DeliveryStatus.FAILED ||
                status == DeliveryStatus.CANCELLED) {
            DeliveryPartner partner = deliveryPartnerRepository
                    .findById(delivery.getDeliveryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Delivery partner not found"));
            if (partner.getPartnerStatus() == PartnerStatus.ACTIVE) {
                partner.setAvailable(true);
                deliveryPartnerRepository.save(partner);
            }

        }
        delivery.setDeliveryStatus(status);
        return deliveryRepository.save(delivery);
    }

    @Transactional(readOnly = true)
    public Delivery getDeliveryById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not Found"));
    }
}