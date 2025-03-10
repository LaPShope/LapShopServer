package com.example.demo.Service.Impl;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.Set;

@Service
public class RedisService {

    private final JedisPool jedisPool;

    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void set(String key, String value, long expireSeconds) {
        try (Jedis jedis = getJedis()) {
            jedis.setex(key, expireSeconds, value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.get(key);
        }
    }

    public boolean exists(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(key);
        }
    }

    public void del(String key) {
        try (Jedis jedis = getJedis()) {
            jedis.del(key);
        }
    }

    public void hset(String key, String field, String value) {
        try (Jedis jedis = getJedis()) {
            jedis.hset(key, field, value);
        }
    }

    public ScanResult<String> scan(String cursor, String pattern, int count) {
        try (Jedis jedis = getJedis()) {
            ScanParams scanParams = new ScanParams().match(pattern).count(count);
            return jedis.scan(cursor, scanParams);
        }
    }

    public void sadd(String key, String value, long expireSeconds) {
        try (Jedis jedis = getJedis()) {
            if (jedis.smembers(key).isEmpty()) {
                jedis.sadd(key, value);
                jedis.expire(key, expireSeconds);
            } else {
                jedis.sadd(key, value);
            }
        }
    }

    public boolean sismember(String key, String value) {
        try (Jedis jedis = getJedis()) {
            return jedis.sismember(key, value);
        }
    }

    public void srem(String key, String value) {
        try (Jedis jedis = getJedis()) {
            jedis.srem(key, value);
        }
    }

    public Set<String> smembers(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.smembers(key);
        }
    }

    public void expire(String key, long seconds) {
        try (Jedis jedis = getJedis()) {
            jedis.expire(key, seconds);
        }
    }


    public Set<String> keys(String pattern) {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(pattern);
        }
    }
}
