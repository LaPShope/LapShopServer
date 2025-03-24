package com.example.demo.service.impl;

import com.example.demo.dto.response.ChatResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import java.util.List;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    // Lưu chuỗi với thời gian hết hạn
    public void set(String key, String value, long expireSeconds) {
        redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    // Lưu Object dưới dạng JSON
    public <T> void setObject(String key, T object, long expireSeconds) {
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(key, jsonString, expireSeconds, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException( e.getMessage());
        }
    }

    // Lấy chuỗi
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    // Lấy Object từ JSON
    public <T> T getObject(String key, TypeReference<T> typeRef) {
        String jsonString = (String) redisTemplate.opsForValue().get(key);
        if (jsonString == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonString, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi chuyển JSON thành object", e);
        }
    }

    // Lưu tin nhắn vào Redis (chuyển Object -> JSON)
    public void saveChatList(String key, List<ChatResponse> chatList, long expireSeconds) {
        try {
            String json = objectMapper.writeValueAsString(chatList);
            redisTemplate.opsForValue().set(key, json, expireSeconds, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi chuyển danh sách ChatResponse thành JSON", e);
        }
    }

    public List<ChatResponse> getChatList(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json == null) {
            return List.of(); // Trả về danh sách rỗng nếu không có dữ liệu
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<ChatResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi chuyển JSON thành danh sách ChatResponse", e);
        }
    }

    // Kiểm tra key có tồn tại không
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteByPatterns(List<String> patterns) {
        Set<String> keysToDelete = new HashSet<>();

        for (String pattern : patterns) {
            try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                    redisConnection -> redisConnection.scan(
                            ScanOptions.scanOptions().match(pattern).count(1000).build()
                    ))) {
                while (cursor.hasNext()) {
                    keysToDelete.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error scanning keys with pattern: " + pattern, e);
            }
        }

        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }

    // Xóa key
    public void del(String key) {
        redisTemplate.delete(key);
    }

    // Thiết lập thời gian hết hạn
    public void expire(String key, long seconds) {
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    // Thao tác với Set
    public void sadd(String key, String value, long expireSeconds) {
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
    }

    public boolean sismember(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    public void srem(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public Set<Object> smembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // Hash Operations
    public void hset(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
}
