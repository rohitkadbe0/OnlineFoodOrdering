package com.example.userservice.controller;

import com.example.userservice.dto.LogIn;
import com.example.userservice.dto.Register;
import com.example.userservice.entity.User;
import com.example.userservice.service.JwtService;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
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
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogIn request) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );
            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
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

    @GetMapping("Id/{userId}")
    public ResponseEntity<?> getById(@PathVariable Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(userService.toResponse(user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found");
        }
    }
}