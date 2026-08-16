package com.example.userservice.controller;

import com.example.userservice.dto.LogIn;
import com.example.userservice.dto.Register;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class userController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogIn request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Username or Password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registor(@RequestBody Register register) {
        User user = new User();
        user.setUsername(register.getUsername().toUpperCase());
        user.setPassword(register.getPassword());
        user.setEmail(register.getEmail());
        user.setRole(register.getRole());
        user.setAddress(register.getAddress().toUpperCase());
        user.setMobileNumber(register.getMobileNumber());
        userService.register(user);
        return ResponseEntity.ok("Register Successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/name/{userName}")
    public ResponseEntity<?> findByUserName(@PathVariable String userName) {
        Optional<User> user = userService.findByUserName(userName);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User Not Found");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<?> createAdmin(@RequestBody Register register) {
        User user = new User();
        user.setUsername(register.getUsername().toUpperCase());
        user.setPassword(register.getPassword());
        user.setEmail(register.getEmail());
        user.setRole(register.getRole());
        user.setAddress(register.getAddress().toUpperCase());
        user.setMobileNumber(register.getMobileNumber());
        userService.adminRegister(user);
        return ResponseEntity.ok("Admin register Successfully");
    }
}