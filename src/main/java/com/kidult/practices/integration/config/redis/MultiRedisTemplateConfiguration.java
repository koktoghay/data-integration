package com.kidult.practices.integration.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author Created by tommy on 2022/09/13.
 */
@Slf4j
@Configuration
public class MultiRedisTemplateConfiguration {

    private String prefix = "spring.redis.lettuce.pool.";

    private String prefix2 = "spring.redis2.lettuce.pool.";

    @Autowired
    private Environment environment;

    @Bean
    public RedisTemplate<String, ?> redisTemplate() {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, ?> redisTemplate2() {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setConnectionFactory(lettuceConnectionFactory2());
        return redisTemplate;
    }

    @Bean
    public GenericObjectPoolConfig redisPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(environment.getProperty(prefix + "max-idle", Integer.class));
        genericObjectPoolConfig.setMaxTotal(environment.getProperty(prefix + "max-active", Integer.class));
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(environment.getProperty(prefix + "max-wait", Integer.class)));
        genericObjectPoolConfig.setMinIdle(environment.getProperty(prefix + "min-idle", Integer.class));
        return genericObjectPoolConfig;
    }

    @Bean
    public GenericObjectPoolConfig redisPool2() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(environment.getProperty(prefix2 + "max-idle", Integer.class));
        genericObjectPoolConfig.setMaxTotal(environment.getProperty(prefix2 + "max-active", Integer.class));
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(environment.getProperty(prefix2 + "max-wait", Integer.class)));
        genericObjectPoolConfig.setMinIdle(environment.getProperty(prefix2 + "min-idle", Integer.class));
        return genericObjectPoolConfig;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    @Primary
    public RedisStandaloneConfiguration redisConf() {
        return new RedisStandaloneConfiguration();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis2")
    public RedisStandaloneConfiguration redisConf2() {
        return new RedisStandaloneConfiguration();
    }

    @Bean
    @Primary
    public LettuceConnectionFactory lettuceConnectionFactory() {
        GenericObjectPoolConfig redisPool = redisPool();
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(redisPool).commandTimeout(redisPool.getMaxWaitDuration()).build();
        return new LettuceConnectionFactory(redisConf(), clientConfiguration);
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory2() {
        GenericObjectPoolConfig redisPool2 = redisPool2();
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(redisPool2).commandTimeout(redisPool2.getMaxWaitDuration()).build();
        return new LettuceConnectionFactory(redisConf2(), clientConfiguration);
    }

}
