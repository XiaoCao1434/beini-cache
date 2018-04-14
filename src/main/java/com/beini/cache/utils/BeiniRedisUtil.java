package com.beini.cache.utils;

import org.springframework.data.redis.core.RedisTemplate;
/**
 * redisUtil公共被集成类
 * @author lb_chen
 */
public class BeiniRedisUtil {
	RedisTemplate<String, Object> redisTemplate;
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
