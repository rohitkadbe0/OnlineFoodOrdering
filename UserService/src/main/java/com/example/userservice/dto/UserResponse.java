package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//this class is used to transfer information to the delivery service using the feign

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private String address;
    private String mobileNumber;
    private List<String> roles;
}
