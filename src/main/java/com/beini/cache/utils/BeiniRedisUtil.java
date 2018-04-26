package com.beini.cache.utils;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
/**
 * redisUtil公共被集成类
 * @author lb_chen
 */
@Component
public abstract class BeiniRedisUtil {
	@Resource(name="beiniRedisTemplate")
	RedisTemplate<String, Object> redisTemplate;
}
