package com.example.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }
@Test
    void generatePassword(){
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    System.out.println(encoder.encode("rohit"));
}

}
