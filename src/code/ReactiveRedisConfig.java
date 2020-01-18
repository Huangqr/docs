package com.think.thinkinjava.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate(factory, redisSerializationContext());
    }

    @Bean
    RedisSerializationContext redisSerializationContext() {
        return new MyRedisSerialization();
    }

    class MyRedisSerialization implements RedisSerializationContext {

        @Override
        public SerializationPair getKeySerializationPair() {
            return SerializationPair.fromSerializer(new StringRedisSerializer());
        }

        @Override
        public SerializationPair getValueSerializationPair() {
            return SerializationPair.fromSerializer(jackson2JsonRedisSerializer());
        }

        @Override
        public SerializationPair<String> getStringSerializationPair() {
            return SerializationPair.fromSerializer(RedisSerializer.string());
        }

        @Override
        public SerializationPair getHashValueSerializationPair() {
            return SerializationPair.fromSerializer(jackson2JsonRedisSerializer());
        }

        @Override
        public SerializationPair getHashKeySerializationPair() {
            return SerializationPair.fromSerializer(new StringRedisSerializer());
        }
    }

    private Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }

}
