package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {

    @Value("${app.redis.host:localhost}")
    private String redisHost;
    @Value("${app.redis.port:6379}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20); // Số kết nối tối đa
        poolConfig.setMaxIdle(10);  // Số kết nối nhàn rỗi tối đa
        poolConfig.setMinIdle(2);   // Giữ ít nhất 2 kết nối nhàn rỗi
        poolConfig.setBlockWhenExhausted(true); // Chặn thay vì ném lỗi khi hết kết nối
        poolConfig.setMaxWaitMillis(2000); // Deprecated

        poolConfig.setMinEvictableIdleTimeMillis(60000); // 60s xóa kết nối nhàn rỗi
        poolConfig.setTimeBetweenEvictionRunsMillis(30000); // Mỗi 30s kiểm tra
        poolConfig.setNumTestsPerEvictionRun(3);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling().poolConfig(poolConfig).build();

        return new JedisConnectionFactory(configuration, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        // Serializer cho key và hash key
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Serializer cho value và hash value
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
