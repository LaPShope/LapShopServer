package com.example.demo.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import({RedisService.class, RedisTemplate.class})
public class RedisServiceTest {

    private final RedisService redisService;

    private final String token = UUID.randomUUID().toString();

    public RedisServiceTest(RedisService redisService) {
        this.redisService = redisService;
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testResetPassword_Redis() {
        String mail = "abc@gmail.com";

        redisService.set(token, mail, 60 * 60 * 24); // 1 ngày
        String value = redisService.get(token);


        System.out.println(mail + " " + value );

        // Kiểm tra giá trị đã lưu trong Redis
    }

    @AfterEach
    void tearDown() {
        // Xóa dữ liệu sau khi test
        redisService.del(this.token);
    }
}
