package com.coatardbul.river.service.impl;

import com.coatardbul.river.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2021/12/20
 *
 * @author Su Xiaolei
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void set() {
        redisTemplate.opsForValue().set("hello","value",12, TimeUnit.MINUTES);

        Object hello = redisTemplate.opsForValue().get("hello");

        int i=1;
    }
}
