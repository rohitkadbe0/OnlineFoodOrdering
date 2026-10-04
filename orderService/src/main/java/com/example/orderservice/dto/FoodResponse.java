package com.example.orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FoodResponse {
    private Long foodId;
    private Long restaurantId;
    private String foodName;
    private String restaurantName;
    private String description;
    private BigDecimal price;
    private String category;
    private Boolean available;
}
