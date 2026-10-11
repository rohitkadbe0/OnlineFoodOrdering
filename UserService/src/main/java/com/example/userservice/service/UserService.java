package com.example.userservice.service;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.enums.Role;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //This method is present in the userDetailsService used for login purpose
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    public User register(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin registration is not allowed.");
        }
        return userRepository.save(user);
    }

    public User adminRegister(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.ADMIN);
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUserName(String name) {
        return userRepository.findByUsername(name);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getAddress(),
                user.getMobileNumber(),
                List.of(user.getRole().name())
        );
    }
}
