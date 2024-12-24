package com.example.ryanazrian.livechat.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("localhost");
        jedisConnectionFactory.setPort(6379);
        // Optional: Uncomment to set password if Redis requires authentication
        // jedisConnectionFactory.setPassword("yourpassword");
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        // Configure JSON serializer for values
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
                new ObjectMapper()
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        );
        template.setValueSerializer(serializer);
        return template;
    }
}
