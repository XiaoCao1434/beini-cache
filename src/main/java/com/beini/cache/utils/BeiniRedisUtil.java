package com.beini.cache.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
/**
 * redisUtil公共被集成类
 * @author lb_chen
 */
@Component
public abstract class BeiniRedisUtil {
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
}
