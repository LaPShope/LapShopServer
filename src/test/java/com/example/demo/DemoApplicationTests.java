package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {
    class Calulator {
        public int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    void contextLoads() {

    }

    @Test
    void testAdd() {
        assertEquals(5, new Calulator().add(2, 3));
    }


}
