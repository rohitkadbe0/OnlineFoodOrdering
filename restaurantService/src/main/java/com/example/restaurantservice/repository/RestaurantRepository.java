package com.example.restaurantservice.repository;

import com.example.restaurantservice.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByRestaurantName(String restaurantName);

    Optional<Restaurant> findByRestaurantId(Long id);

}
