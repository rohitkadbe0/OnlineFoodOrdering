package com.example.restaurantservice.controller;

import com.example.restaurantservice.entity.Restaurant;
import com.example.restaurantservice.enums.ApprovalStatus;
import com.example.restaurantservice.enums.RestaurantStatus;
import com.example.restaurantservice.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Restaurant> getAll() {
        return restaurantService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant) {
        restaurantService.saveEntry(restaurant);
        return ResponseEntity.ok(restaurant);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @GetMapping("/restaurantId/{restaurantId}")
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable Long restaurantId) {
        Optional<Restaurant> byRestaurantId = restaurantService.findByRestaurantId(restaurantId);
        return byRestaurantId
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/restaurantName/{restaurantName}")
    public ResponseEntity<Restaurant> findByName(@PathVariable String restaurantName) {
        Optional<Restaurant> byRestaurantName = restaurantService.findByName(restaurantName);
        return byRestaurantName
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @DeleteMapping("/restaurantId/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long restaurantId) {
        boolean deleted = restaurantService.deleteRestaurant(restaurantId);
        if (deleted) {
            return ResponseEntity.ok("restaurant deleted successfully");

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("restaurant not found");
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/approval/{restaurantId}")
    public ResponseEntity<?> updateApprovalStatus(
            @PathVariable Long restaurantId, @RequestParam ApprovalStatus status
            ){
        boolean update =restaurantService.updateApprovalStatus(
                restaurantId,status
        );
        if(update){
            return ResponseEntity.ok("Restaurant status update to "+status);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Restaurant not found");
    }
@GetMapping("/status/name/{restaurantName}")
public ResponseEntity<?> getRestaurantStatus(@PathVariable String restaurantName){
        Optional<Restaurant> restaurant = restaurantService.findByName(restaurantName);
        if(restaurant.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Restaurant r= restaurant.get();
        if(restaurantService.isRestaurantOpen(r)){
            r.setRestaurantStatus(RestaurantStatus.OPEN);
        }else{
            r.setRestaurantStatus(RestaurantStatus.CLOSED);
        }
        return ResponseEntity.ok(r);
}

}
