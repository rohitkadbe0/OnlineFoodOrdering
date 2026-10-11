package com.example.deliveryservice.dto;

import com.example.deliveryservice.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReadyEvent {
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String pickUpAddress;
    private String deliveryAddress;
    private DeliveryStatus status;
}
