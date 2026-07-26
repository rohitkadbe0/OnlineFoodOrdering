package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogIn {
    @NonNull
    private String username;
    @NonNull
    private String password;
    private List<String> roles;
}
