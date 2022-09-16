package com.kidult.practices.integration.controller.redis;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by tommy on 2022/09/16.
 */
@RestController
@RequestMapping("/redissonOpt")
public class RedissonOptController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/setStrKV")
    public Object setStrKV(String k, String v) {
        redissonClient.getBucket(k).set(v);
        return redissonClient.getBucket(k).get();
    }
}
