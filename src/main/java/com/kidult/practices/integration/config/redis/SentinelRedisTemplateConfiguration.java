package com.kidult.practices.integration.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Created by tommy on 2022/09/14.
 */
@Slf4j
@Configuration
public class SentinelRedisTemplateConfiguration {

    @Value("${spring.redis.sentinel.master}")
    private String sentinelMaster;
    @Value("${spring.redis.sentinel.nodes}")
    private String sentinelNodes;

    @Value("${spring.rediss.sentinel.master}")
    private String sentinelMaster2;
    @Value("${spring.rediss.sentinel.nodes}")
    private String sentinelNodes2;


    @Bean
    public RedisTemplate<String, ?> sentinelRedisTemplate() {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setConnectionFactory(lettuceSentinelConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, ?> sentinelRedisTemplate2() {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setConnectionFactory(lettuceSentinelConnectionFactory2());
        return redisTemplate;
    }


    @Bean
    @Primary
    public RedisSentinelConfiguration redisSentinelConf() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        String[] nodes = sentinelNodes.split(",");
        for (String host : nodes) {
            String[] node = host.split(":");
            redisSentinelConfiguration.addSentinel(new RedisNode(node[0], Integer.parseInt(node[1])));
        }
        redisSentinelConfiguration.setMaster(sentinelMaster);
        return redisSentinelConfiguration;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConf2() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        String[] nodes = sentinelNodes2.split(",");
        for (String host : nodes) {
            String[] node = host.split(":");
            redisSentinelConfiguration.addSentinel(new RedisNode(node[0], Integer.parseInt(node[1])));
        }
        redisSentinelConfiguration.setMaster(sentinelMaster2);
        return redisSentinelConfiguration;
    }

    @Bean
    public RedisConnectionFactory lettuceSentinelConnectionFactory() {
        return new LettuceConnectionFactory(redisSentinelConf());
    }

    @Bean
    public RedisConnectionFactory lettuceSentinelConnectionFactory2() {
        return new LettuceConnectionFactory(redisSentinelConf2());
    }

}
