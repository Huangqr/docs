package com.holderzone.framework.dynamic.datasource.starter.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.holderzone.framework.dynamic.datasource.starter.base.BaseConfig;
import com.holderzone.framework.dynamic.datasource.starter.utils.DynamicInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Import(BaseConfig.class)
@Configuration
@ConditionalOnProperty(prefix = "dynamic.redis", value = "enabled", havingValue = "true")
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);


    @Bean(name = "redisFactoryCache")
    public Cache<String, JedisConnectionFactory> redisConnectionCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(12, TimeUnit.HOURS)
                .removalListener(new RemovalListener<String, JedisConnectionFactory>() {

                    @Override
                    public void onRemoval(RemovalNotification<String, JedisConnectionFactory> notification) {
                        JedisConnectionFactory factory = notification.getValue();
                        log.info("清除JedisConnectioFactory,{}", factory.getHostName() + ":" + factory.getDatabase());
                        factory.destroy();
                    }
                })
                .build();

    }

    @Bean
    public DynamicRedisConnectionFactory dynamicRedisConnectionFactory(@Qualifier("redisFactoryCache") Cache<String, JedisConnectionFactory> redisConnectionCache,
                                                                       DynamicInfoHelper dynamicInfoHelper) {
        return new DynamicRedisConnectionFactory(redisConnectionCache, dynamicInfoHelper);
    }


    @Bean
    @ConditionalOnBean(DynamicRedisConnectionFactory.class)
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate redisTemplate(DynamicRedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        setSerializeForRedisTemplate(template);
        template.afterPropertiesSet();
        return template;
    }

    private void setSerializeForRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = getObjectMapper();
        serializer.setObjectMapper(mapper);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 设置时区
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        //设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略空bean转json的错误
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //转换localDatetime
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}
