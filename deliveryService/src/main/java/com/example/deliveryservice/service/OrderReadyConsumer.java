package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderReadyConsumer {
    private final DeliveryService deliveryService;
    @KafkaListener(
            topics = "order-ready-for-pickup",
            groupId = "delivery-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    //this method is used automatically by Spring Kafka, even if you never call it directly from another Java method.
    public void consumeOrderReady(OrderReadyEvent event){
        System.out.println(
                "Received order-ready event for order:"+event.getOrderId()
        );
        if (!"READY_FOR_PICKUP".equals(event.getStatus())){
            System.out.println("Ignoring event with status"+event.getStatus());
            return;
        }
        deliveryService.addDelivery(event);
    }

}
