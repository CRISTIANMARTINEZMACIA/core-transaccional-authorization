package com.golden_clear.core_transaccional_authorization.shared.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisSerializer<Object> serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("com.findcard.authorization")
                                .build()
                )
                .build();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(30))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("cardBalances", config.entryTtl(Duration.ofSeconds(30)))
                .withCacheConfiguration("cardLimits", config.entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}