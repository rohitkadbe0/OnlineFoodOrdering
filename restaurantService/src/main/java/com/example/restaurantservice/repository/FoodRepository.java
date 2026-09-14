package com.example.restaurantservice.repository;

import com.example.restaurantservice.entity.Food;
import com.example.restaurantservice.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByRestaurantName(String restaurantName);

    Optional<Food> findByRestaurantId(Long id);

    Optional<Food> findByFoodId(Long id);

    Optional<Food> findByName(String foodName);
}
