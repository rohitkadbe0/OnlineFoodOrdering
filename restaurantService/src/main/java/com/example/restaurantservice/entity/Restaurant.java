package com.example.restaurantservice.entity;

import com.example.restaurantservice.enums.ApprovalStatus;
import com.example.restaurantservice.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor

public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;
    private Long ownerId;
    private String restaurantName;
    private String address;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String phoneNumber;
    private LocalTime openingTime;
    private LocalTime closingTime;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    @Enumerated(EnumType.STRING)
    private RestaurantStatus restaurantStatus;
    @Enumerated(EnumType.STRING)
    private DayOfWeek weeklyOff;
}
