package com.example.deliveryservice.entity;

import com.example.deliveryservice.enums.PartnerStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerStatus partnerStatus;
    private boolean available=true;
    private LocalDateTime createdAt;
}
