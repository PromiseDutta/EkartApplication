package com.info.ekart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
    	return RedisCacheConfiguration.defaultCacheConfig()
    	        // Creates a default Redis cache configuration
    	        // Includes default key serializer, value serializer, etc.

    	        .entryTtl(Duration.ofMinutes(10))
    	        // Sets TTL (Time To Live)
    	        // Each cache entry will automatically expire after 10 minutes
    	        // After 10 minutes → Redis deletes that key

    	        .disableCachingNullValues()
    	        // Prevents caching null results
    	        // If DB returns null → it will NOT be stored in Redis
    	        // Avoids caching "empty" responses permanently

    	        .serializeValuesWith(
    	                RedisSerializationContext.SerializationPair
    	                        .fromSerializer(new GenericJackson2JsonRedisSerializer())
    	        );
    	        // Configures how values are stored in Redis
    	        // By default Spring uses JDK binary serialization
    	        // Here we replace it with JSON serialization
    	        // So objects are stored as readable JSON in Redis
    	        // No need to implement Serializable in DTO
    }
    
    

@Bean
public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    // Use String serializers for both key and value
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.afterPropertiesSet();
    return template;
}
}