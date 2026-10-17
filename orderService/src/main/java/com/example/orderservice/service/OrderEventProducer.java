package com.example.orderservice.service;

import com.example.orderservice.dto.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private static final String TOPIC =
            "order-ready-for-pickup";
    private final KafkaTemplate<String, OrderReadyEvent> kafkaTemplate;

    public void publishOrderReady(OrderReadyEvent event) {

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(event.getOrderId()),
                event
        );
    }
}
