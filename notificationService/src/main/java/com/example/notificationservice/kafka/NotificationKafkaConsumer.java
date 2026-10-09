package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.OrderStatusEvent;
import com.example.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class NotificationKafkaConsumer {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(
            topics = "ORDER_STATUS_UPDATE",
            groupId = "notification-group"
    )
    public void consumeOrderStatus(String message) {
        try {
            OrderStatusEvent event = objectMapper.readValue(message, OrderStatusEvent.class);
            System.out.println("Received order status event:" + message);
            notificationService.createNotification(
                    event.getCustomerId(),
                    event.getOrderId(),
                    event.getCustomerEmail() ,
                    event.getStatus()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
