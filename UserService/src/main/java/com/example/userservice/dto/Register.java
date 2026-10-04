package com.example.userservice.dto;

import com.example.userservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class Register {
    @NonNull
    private String username;
    @NonNull
    private String password;
    private String email;
    @NonNull
    private Role role;
    private String address;
    private String mobileNumber;
}
