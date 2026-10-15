package com.example.orderservice.service;

import com.example.orderservice.dto.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderReadyEvent> kafkaTemplate;
    private static final String TOPIC =
            "order-ready-for-pickup";
    public void publishOrderReady(OrderReadyEvent event){
        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        ).whenComplete((result, exception)->{
            if (exception!=null){
                System.err.println(
                        "Failed to publish order event:"
                        +exception.getMessage()
                );
            }else {
                System.out.println(
                        "Order event published. OrderID: "+event.getOrderId()
                );
            }
        });
    }
}
