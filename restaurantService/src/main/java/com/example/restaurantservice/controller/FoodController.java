package com.example.restaurantservice.controller;

import com.example.restaurantservice.entity.Food;
import com.example.restaurantservice.enums.ApprovalStatus;
import com.example.restaurantservice.enums.FoodStatus;
import com.example.restaurantservice.enums.RestaurantStatus;
import com.example.restaurantservice.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    private FoodService foodService;

    @GetMapping("/available")
    public ResponseEntity<List<Food>> getAll() {
        return ResponseEntity.ok(foodService.getAvailableFood());
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @PostMapping
    public ResponseEntity<Food> addFood(@RequestBody Food food) {
        foodService.saveEntry(food);
        return ResponseEntity.ok(food);
    }

    @GetMapping("/restaurantName/{restaurantName}")
    public ResponseEntity<?> findByRestaurantName(
            @PathVariable String restaurantName) {

        List<Food> foods =
                foodService.findByRestaurantName(restaurantName);

        List<Food> availableFoods = foods.stream()
                .filter(food ->
                        food.getFoodStatus() == FoodStatus.AVAILABLE
                                && food.getRestaurantStatus() == RestaurantStatus.OPEN
                                && food.getApprovalStatus() == ApprovalStatus.APPROVED
                )
                .toList();

        if (availableFoods.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(availableFoods);
    }

    @GetMapping("/restaurantId/{restaurantId}")
    public ResponseEntity<?> findByRestaurantId(
            @PathVariable Long restaurantId) {

        List<Food> foods =
                foodService.findByRestaurantId(restaurantId);

        List<Food> availableFoods = foods.stream()
                .filter(food ->
                        food.getFoodStatus() == FoodStatus.AVAILABLE
                                && food.getRestaurantStatus() == RestaurantStatus.OPEN
                                && food.getApprovalStatus() == ApprovalStatus.APPROVED
                )
                .toList();

        if (availableFoods.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(availableFoods);
    }

    @GetMapping("/foodId/{foodId}")
    public ResponseEntity<Food> findByFoodId(@PathVariable Long foodId) {
        Optional<Food> byRestaurantId = foodService.findByFoodId(foodId);
        Food f = byRestaurantId.get();
        if (f.getFoodStatus() == FoodStatus.AVAILABLE
                && f.getRestaurantStatus() == RestaurantStatus.OPEN
                && f.getApprovalStatus() == ApprovalStatus.APPROVED) {
            return ResponseEntity.ok(f);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/foodName/{foodName}")
    public ResponseEntity<?> findByFoodName(
            @PathVariable String foodName) {

        List<Food> foods =
                foodService.findByFoodName(foodName);

        if (foods.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Food> availableFoods = foods.stream()
                .filter(food ->
                        food.getFoodStatus() == FoodStatus.AVAILABLE
                                && food.getRestaurantStatus() == RestaurantStatus.OPEN

                                && food.getApprovalStatus() == ApprovalStatus.APPROVED
                )
                .toList();

        if (availableFoods.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(availableFoods);
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @PutMapping("/Id/{foodId}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long foodId,
            @RequestBody Food food
    ) {
        Optional<Food> existingFood = foodService.findByFoodId(foodId);
        if (existingFood.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Food oldFood = existingFood.get();
        oldFood.setFoodName(food.getFoodName());
        oldFood.setDescription(food.getDescription());
        oldFood.setPrice(food.getPrice());
        oldFood.setCategory(food.getCategory());
        oldFood.setFoodStatus(food.getFoodStatus());
        Food updateFood = foodService.updateFood(oldFood);
        return ResponseEntity.ok(updateFood);
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @DeleteMapping("/foodId/{foodId}")
    public ResponseEntity<?> deleteFood(@PathVariable Long foodId) {
        boolean deleted = foodService.deleteFood(foodId);
        if (deleted) {
            return ResponseEntity.ok("Food deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food Not Found");
        }
    }
}
