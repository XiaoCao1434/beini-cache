package com.beini.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.beini.cache.utils.BeiniAllRedisUtil;
import com.beini.cache.utils.BeiniListRedisUtil;
import com.beini.cache.utils.BeiniMapRedisUtil;
import com.beini.cache.utils.BeiniSetRedisUtil;
import com.beini.cache.utils.BeiniStringRedisUtil;

import redis.clients.jedis.JedisPoolConfig;

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

	@Bean
	public BeiniAllRedisUtil getBeiniAllRedisUtil() {
		return new BeiniAllRedisUtil();
	}

	@Bean("beiniRedisTemplate")
	public RedisTemplate<?, ?> beiniRedisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		// key序列化方式;但是如果方法上有Long等非String类型的话，会报类型转换错误；
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();// Long类型不可以会出现异常信息;
		redisTemplate.setDefaultSerializer(redisSerializer);
		/*JdkSerializationRedisSerializer序列化方式,也是spring-data-redis的默认序列化方式;*/
		//JdkSerializationRedisSerializer jdkRedisSerializer = new JdkSerializationRedisSerializer();
		//redisTemplate.setValueSerializer(jdkRedisSerializer);
		//redisTemplate.setHashValueSerializer(jdkRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public RedisConnectionFactory getRedisConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName("127.0.0.1");
		factory.setPort(6379);
		factory.setPassword("");
		// 存储的库
		factory.setDatabase(0);
		// 设置连接超时时间
		factory.setTimeout(60000);
		factory.setUsePool(true);
		factory.setPoolConfig(jedisPoolConfig());
		return factory;

	}

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(10000);
		jedisPoolConfig.setMinIdle(100);
		return jedisPoolConfig;
	}
}
