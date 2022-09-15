package com.kidult.practices.integration.controller.redis;

import com.kidult.practices.integration.domain.RedisDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by tommy on 2022/09/13.
 */
@RestController
@RequestMapping("/multiRedisOpt")
public class MultiRedisOptController {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    @Qualifier("redisTemplate2")
    private RedisTemplate redisTemplate2;

    @GetMapping("/{redisIndex}/setStrKV")
    public Object setStrKV(String k, String v, @PathVariable("redisIndex") String redisIndex) {
        if ("1".equals(redisIndex)) {
            redisTemplate.opsForValue().set(k, v);
        } else {
            redisTemplate2.opsForValue().set(k, v);
        }
        if ("1".equals(redisIndex)) {
            return redisTemplate.opsForValue().get(k);
        } else {
            return redisTemplate2.opsForValue().get(k);
        }
    }

    @GetMapping("/{redisIndex}/setDomainKV")
    public Object setDomainKV(@PathVariable("redisIndex") String redisIndex) {
        RedisDomain redisDomain = RedisDomain.of("liebao", "3512", "tommy");
        if ("1".equals(redisIndex)) {
            redisTemplate.opsForValue().set("redisDomain", redisDomain);
        } else {
            redisTemplate2.opsForValue().set("redisDomain", redisDomain);
        }
        if ("1".equals(redisIndex)) {
            return redisTemplate.opsForValue().get("redisDomain");
        } else {
            return redisTemplate2.opsForValue().get("redisDomain");
        }
    }

}
