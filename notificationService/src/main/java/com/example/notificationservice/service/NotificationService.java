package com.example.notificationservice.service;

import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EmailService emailService;

    public void createNotification(
            Long customerId,
            Long orderId,
            String customerEmail,
            NotificationStatus status
    ) {
        String message;
        switch (status) {
            case CONFIRMED:
                message = "your order is confirmed";
                break;
            case DELIVERY_BOY_ALLOTTED:
                message = "Delivery boy has been allotted";
                break;
            case OUT_FOR_DELIVERY:
                message = "Your order is out for delivery";
                break;
            case NEAR_LOCATION:
                message = "Delivery partner has reached near your location";
                break;
            case REACHED_LOCATION:
                message = "Delivery partner has reached your location";
                break;
            case DELIVERED:
                message = "Order has been delivered";
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown notification status: " + status
                );
        }
        Notification notification = new Notification();
        notification.setCustomerId(customerId);
        notification.setOrderId(orderId);
        notification.setCustomerEmail(customerEmail);
        notification.setMessage(message);
        notification.setNotificationDate(LocalDateTime.now());
        notification.setSent(false);

        Notification savedNotification = notificationRepository.save(notification);
        try {
            emailService.sendEmail(
                    customerEmail,
                    "Food Order Update - Order #" + orderId, message
            );
            savedNotification.setSent(true);
            notificationRepository.save(savedNotification);
        } catch (Exception e) {
            throw new RuntimeException("failed to send email " + e.getMessage());
        }
    }
}
