package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DemoApplicationTests {
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    @Test
    void test_match() {
        String hashed = "$2a$10$5gDDOqlY0cgl7HPfEYrcru9zw972UE4/JzDTJwfBxXBJyZZvljz8G";

        String fake = "1234";

        if (passwordEncoder.matches(fake, hashed)) {
            System.out.println("Password match");
        } else {
            System.out.println("Password does not match");
        }
    }


    @Test
    void contextLoads() {

    }


}
