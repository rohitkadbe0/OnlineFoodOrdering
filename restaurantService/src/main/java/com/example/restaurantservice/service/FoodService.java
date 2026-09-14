package com.example.restaurantservice.service;

import com.example.restaurantservice.entity.Food;
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
        if (food.getFoodName().equalsIgnoreCase("Food")) {
            throw new IllegalArgumentException(
                    "Give correct name of food"
            );
        }
        foodRepository.save(food);
    }

    public List<Food> getAll() {
        return foodRepository.findAll();
    }

    public boolean deleteFood(Long FoodId) {
        if (foodRepository.existsById(FoodId)) {
            foodRepository.deleteById(FoodId);
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
        return foodRepository.findByName(foodName);
    }
}

