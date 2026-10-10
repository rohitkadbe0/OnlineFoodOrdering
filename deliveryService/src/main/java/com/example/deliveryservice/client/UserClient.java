package com.example.deliveryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "userService",
        url = "http://localhost:8080"
)
public interface UserClient {
    @GetMapping("/user/{userID}")
    UserResponse getUserById(
            @PathVariable("userId") Long userId
    );
}
