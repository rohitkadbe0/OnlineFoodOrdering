package com.example.deliveryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="delivery_partners")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryPartnerId;
    @Column(nullable = false,unique = true)
    private Long userId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String vehicleNumber;
    private String vehicleType;
    @Column(nullable = false)
    private Boolean  available=true;
    private LocalDateTime createdAt;
}
