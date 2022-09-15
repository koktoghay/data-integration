package com.kidult.practices.integration.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author Created by tommy on 2022/09/15.
 */
@Configuration
public class ClusterRedisTemplateConfiguration {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.cluster.max-redirects}")
    private Integer maxRedirects;

    @Bean
    public RedisTemplate<String, ?> clusterRedisTemplate() {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setConnectionFactory(lettuceClusterConnectionFactory());
        return redisTemplate;
    }

    @Bean
    @Primary
    public RedisClusterConfiguration redisClusterConf() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        String[] nodes = clusterNodes.split(",");
        for (String host : nodes) {
            String[] node = host.split(":");
            redisClusterConfiguration.addClusterNode(new RedisNode(node[0], Integer.parseInt(node[1])));
        }
        redisClusterConfiguration.setMaxRedirects(maxRedirects);
        return redisClusterConfiguration;
    }

    @Bean
    public RedisConnectionFactory lettuceClusterConnectionFactory() {
        //配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，Redis集群变更时将会导致连接异常
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                //开启周期刷新（默认60秒）
                .enablePeriodicRefresh(Duration.ofSeconds(60))
                .enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.ASK_REDIRECT)
//                .enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.ASK_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.UNKNOWN_NODE)
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                //拓扑刷新
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)
                .socketOptions(SocketOptions.builder().keepAlive(true).build())
                //取消校验集群节点的成员关系
                .validateClusterNodeMembership(false).build();

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clusterClientOptions)
//                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();

        return new LettuceConnectionFactory(redisClusterConf(), lettuceClientConfiguration);
    }

}
