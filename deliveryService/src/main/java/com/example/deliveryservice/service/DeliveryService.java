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
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;



    private static final Map<DeliveryStatus, Set<DeliveryStatus>>
            ALLOWED_TRANSITIONS = Map.of(
            DeliveryStatus.ACCEPTED,
            EnumSet.of(
                    DeliveryStatus.PICKED_UP,
                    DeliveryStatus.FAILED,
                    DeliveryStatus.CANCELLED
            ),

            DeliveryStatus.PICKED_UP,
            EnumSet.of(
                    DeliveryStatus.OUT_FOR_DELIVERY,
                    DeliveryStatus.FAILED,
                    DeliveryStatus.CANCELLED
            ),

            DeliveryStatus.OUT_FOR_DELIVERY,
            EnumSet.of(
                    DeliveryStatus.REACHED_LOCATION,
                    DeliveryStatus.FAILED,
                    DeliveryStatus.CANCELLED
            ),

            DeliveryStatus.REACHED_LOCATION,
            EnumSet.of(
                    DeliveryStatus.DELIVERED,
                    DeliveryStatus.FAILED,
                    DeliveryStatus.CANCELLED
            )
    );

    // Called automatically by the Kafka consumer.
    @Transactional
    public Delivery addDelivery(OrderReadyEvent event) {

        if (event == null
                || event.getOrderId() == null
                || event.getCustomerId() == null
                || event.getRestaurantId() == null
                || !"READY_FOR_PICKUP".equalsIgnoreCase(
                event.getStatus())) {

            throw new IllegalArgumentException(
                    "Invalid order-ready event"
            );
        }

        // Make event processing idempotent.
        var existing = deliveryRepository.findByOrderId(
                event.getOrderId()
        );

        if (existing.isPresent()) {
            return existing.get();
        }

        DeliveryPartner partner =
                deliveryPartnerRepository.findFirstByAvailableTrue()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No available delivery partner found"
                                )
                        );

        Delivery delivery = new Delivery();

        delivery.setOrderId(event.getOrderId());
        delivery.setCustomerId(event.getCustomerId());
        delivery.setRestaurantId(event.getRestaurantId());
        delivery.setDeliveryPartnerId(
                partner.getDeliveryPartnerId()
        );

        delivery.setPickupAddress(event.getPickUpAddress());
        delivery.setDeliveryAddress(event.getDeliveryAddress());

        delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
        delivery.setAssignedAt(LocalDateTime.now());

        // Reserve the partner for this delivery.
        partner.setAvailable(false);
        deliveryPartnerRepository.save(partner);

        return deliveryRepository.save(delivery);
    }

    // Used by the PUT endpoint.
    @Transactional
    public Delivery updateDeliveryStatus(
            Long deliveryId,
            DeliveryStatus newStatus) {

        if (newStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Delivery not found: " + deliveryId
                        )
                );

        DeliveryStatus currentStatus =
                delivery.getDeliveryStatus();

        Set<DeliveryStatus> allowed =
                ALLOWED_TRANSITIONS.getOrDefault(
                        currentStatus,
                        Set.of()
                );

        if (!allowed.contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid delivery status transition: "
                            + currentStatus + " -> " + newStatus
            );
        }

        delivery.setDeliveryStatus(newStatus);

        if (newStatus == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        }

        if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());

            // Make the partner available again.
            DeliveryPartner partner =
                    deliveryPartnerRepository
                            .findById(delivery.getDeliveryPartnerId())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.INTERNAL_SERVER_ERROR,
                                            "Assigned delivery partner not found"
                                    )
                            );

            partner.setAvailable(true);
            deliveryPartnerRepository.save(partner);
        }

        if (newStatus == DeliveryStatus.FAILED
                || newStatus == DeliveryStatus.CANCELLED) {

            DeliveryPartner partner =
                    deliveryPartnerRepository
                            .findById(delivery.getDeliveryPartnerId())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.INTERNAL_SERVER_ERROR,
                                            "Assigned delivery partner not found"
                                    )
                            );

            partner.setAvailable(true);
            deliveryPartnerRepository.save(partner);
        }

        return deliveryRepository.save(delivery);
    }

    public Delivery getDeliveryById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Delivery not found: " + deliveryId
                        )
                );
    }

    public Delivery getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Delivery not found for order: " + orderId
                        )
                );
    }
}