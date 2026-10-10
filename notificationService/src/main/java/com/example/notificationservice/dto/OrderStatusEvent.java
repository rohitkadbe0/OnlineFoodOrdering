package com.example.notificationservice.dto;

import com.example.notificationservice.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusEvent {
    private Long customerId;
    private Long orderId;
    private String customerEmail;
    private NotificationStatus status;
}
