package com.example.restaurantservice.entity;

import com.example.restaurantservice.enums.ApprovalStatus;
import com.example.restaurantservice.enums.FoodStatus;
import com.example.restaurantservice.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;
    private Long restaurantId;
    private String foodName;
    private String restaurantName;
    private String description;
    private BigDecimal price;
    private String category;
    private Boolean available;
    @Enumerated(EnumType.STRING)
    private FoodStatus foodStatus;
    @Enumerated(EnumType.STRING)
    private RestaurantStatus restaurantStatus;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
}
