package com.example.orderservice.client;

import com.example.orderservice.dto.FoodResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "restaurantService",
        url="http://localhost:8081"
)
public interface FoodClient {
    @GetMapping("/foood/foodId/{foodId}")
    FoodResponse getFoodById(@PathVariable Long foodId);
}
