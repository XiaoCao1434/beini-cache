package com.beini.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.beini.cache.utils.BeiniListRedisUtil;
import com.beini.cache.utils.BeiniMapRedisUtil;
import com.beini.cache.utils.BeiniSetRedisUtil;
import com.beini.cache.utils.BeiniStringRedisUtil;

@Configuration
public class RedisUtilConfig {
	
	@Bean
	public BeiniStringRedisUtil getBeiniStringRedisUtil() {
		return new BeiniStringRedisUtil();
	}
	@Bean
	public BeiniListRedisUtil getBeiniListRedisUtil() {
		return new BeiniListRedisUtil();
	}
	@Bean
	public BeiniMapRedisUtil getBeiniMapRedisUtil() {
		return new BeiniMapRedisUtil();
	}
	@Bean
	public BeiniSetRedisUtil getBeiniSetRedisUtil() {
		return new BeiniSetRedisUtil();
	}
}
