package com.example.restaurantservice.service;

import com.example.restaurantservice.entity.Food;
import com.example.restaurantservice.enums.ApprovalStatus;
import com.example.restaurantservice.enums.FoodStatus;
import com.example.restaurantservice.enums.RestaurantStatus;
import com.example.restaurantservice.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;

    public void saveEntry(Food food) {
        if (food.getFoodName() == null ||
                food.getFoodName().isBlank() ||
                food.getFoodName().equalsIgnoreCase("Food")) {
            throw new IllegalArgumentException(
                    "Give correct name of food"
            );
        }
        foodRepository.save(food);
    }

    public List<Food> getAvailableFood() {
        return foodRepository.findAll()
                .stream()
                .filter(food ->
                        food.getFoodStatus() == FoodStatus.AVAILABLE
                                && food.getRestaurantStatus() == RestaurantStatus.OPEN
                                && food.getApprovalStatus() == ApprovalStatus.APPROVED
                )
                .toList();

    }

    public boolean deleteFood(Long foodId) {
        if (foodRepository.existsById(foodId)) {
            foodRepository.deleteById(foodId);
            return true;
        }
        return false;
    }

    public Optional<Food> findByRestaurantName(String restaurantName) {
        return foodRepository.findByRestaurantName(restaurantName);
    }

    public Optional<Food> findByRestaurantId(Long id) {
        return foodRepository.findByRestaurantId(id);
    }

    public Optional<Food> findByFoodId(Long id) {
        return foodRepository.findByFoodId(id);
    }

    public Optional<Food> findByFoodName(String foodName) {
        return foodRepository.findByFoodName(foodName);
    }

    public Food updateFood(Food food) {
        return foodRepository.save(food);
    }
}

