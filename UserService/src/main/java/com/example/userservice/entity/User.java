package com.example.userservice.entity;

import jakarta.persistence.*;
import com.example.userservice.enums.Role;
@Entity
@Table(name= "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    private String username;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String mobileNumber;
    private String address;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
}
