package com.example.deliveryservice.enums;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public enum DeliveryStatus {
    ASSIGNED,
    PICKED_UP,
    OUT_FOR_DELIVERY,
    REACHED_LOCATION,
    DELIVERED,
    FAILED,
    CANCELLED
}
