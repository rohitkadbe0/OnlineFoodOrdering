package com.example.restaurantservice.service;

import com.example.restaurantservice.entity.Restaurant;
import com.example.restaurantservice.enums.ApprovalStatus;
import com.example.restaurantservice.repository.RestaurantRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;


    public void saveEntry(Restaurant restaurant) {
        if(restaurant.getRestaurantName().equalsIgnoreCase("restaurant")){
            throw new IllegalArgumentException(
                    "Restaurant name cannot be 'restaurant'"
            );
        }
     restaurant.setApprovalStatus(ApprovalStatus.PENDING);
        restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> findByName(String restaurantName) {

        return restaurantRepository.findByRestaurantName(restaurantName);
    }

    public Optional<Restaurant> findByRestaurantId(Long id) {
        return restaurantRepository.findByRestaurantId(id);
    }

    public boolean deleteRestaurant(Long restaurantId) {
        if (restaurantRepository.existsById(restaurantId)) {
            restaurantRepository.deleteById(restaurantId);
            return true;
        }
        return false;
    }
    public boolean updateApprovalStatus(
            Long restaurantId, ApprovalStatus status
    ){
        Optional<Restaurant> restaurant=restaurantRepository.findByRestaurantId(restaurantId);
        if (restaurant.isEmpty()){
            return false;
        }
        Restaurant r =restaurant.get();
        r.setApprovalStatus(status);
        restaurantRepository.save(r);
        return true;
    }
    public boolean isRestaurantOpen(Restaurant restaurant){
        LocalDateTime now =LocalDateTime.now();
        if(now.getDayOfWeek()==restaurant.getWeeklyOff()){
            return false;
        }
        LocalTime currentTime = now.toLocalTime();
        return !currentTime.isBefore(restaurant.getOpeningTime())
        && currentTime.isBefore(restaurant.getClosingTime());
    }
}
