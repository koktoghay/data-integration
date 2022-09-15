package com.kidult.practices.integration.controller.redis;

import com.kidult.practices.integration.domain.RedisDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by tommy on 2022/09/15.
 */
@RestController
@RequestMapping("/clusterRedisOpt")
public class ClusterRedisOptController {

    @Autowired
    @Qualifier("clusterRedisTemplate")
    private RedisTemplate redisTemplate;

    @GetMapping("/setStrKV")
    public Object setStrKV(String k, String v) {
        redisTemplate.opsForValue().set(k, v);
        return redisTemplate.opsForValue().get(k);
    }

    @GetMapping("setDomainKV")
    public Object setDomainKV() {
        RedisDomain redisDomain = RedisDomain.of("liebao", "3512", "tommy");
        redisTemplate.opsForValue().set("redisDomain", redisDomain);
        return redisTemplate.opsForValue().get("redisDomain");
    }
}
