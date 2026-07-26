package com.example.restaurantservice.entity;

import com.example.restaurantservice.enums.approvalStatus;
import com.example.restaurantservice.enums.restaurantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Restaurant {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long Id;
        @Column(unique = true)
        private long ownerId;
        private String restaurantName;
        private String address;
        @Column(unique = true)
        private String email;
        @Column(unique = true)
        private String phoneNumber;
        private LocalTime openingTime;
        private LocalTime closingTime;
        @Enumerated(EnumType.STRING)
        private approvalStatus approvalStatus;
        @Enumerated(EnumType.STRING)
        private restaurantStatus restaurantStatus;

}
