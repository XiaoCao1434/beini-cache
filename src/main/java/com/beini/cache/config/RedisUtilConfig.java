package com.beini.cache.config;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.ClassUtils;

import com.beini.cache.utils.BeiniAllRedisUtil;
import com.beini.cache.utils.BeiniListRedisUtil;
import com.beini.cache.utils.BeiniMapRedisUtil;
import com.beini.cache.utils.BeiniStringRedisUtil;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
@Slf4j
public class RedisUtilConfig extends CachingConfigurerSupport {

	@Bean("myken")
	@Override
	public KeyGenerator keyGenerator() {
		System.out.println("RedisCacheConfig.keyGenerator()");
		return new KeyGenerator() {
			 // custom cache key  
		    public static final int NO_PARAM_KEY = 0;  
		    public static final int NULL_PARAM_KEY = 53;  
		    @Override  
		    public Object generate(Object target, Method method, Object... params) {  
		  
		        StringBuilder key = new StringBuilder();  
		        key.append(target.getClass().getSimpleName()).append(".").append(method.getName()).append(":");  
		        if (params.length == 0) {  
		            return key.append(NO_PARAM_KEY).toString();  
		        }  
		        for (Object param : params) {  
		            if (param == null) {  
		                log.warn("input null param for Spring cache, use default key={}", NULL_PARAM_KEY);  
		                key.append(NULL_PARAM_KEY);  
		            } else if (ClassUtils.isPrimitiveArray(param.getClass())) {  
		                int length = Array.getLength(param);  
		                for (int i = 0; i < length; i++) {  
		                    key.append(Array.get(param, i));  
		                    key.append(',');  
		                }  
		            } else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {  
		                key.append(param);  
		            } else {  
		                log.warn("Using an object as a cache key may lead to unexpected results. " +  
		                        "Either use @Cacheable(key=..) or implement CacheKey. Method is " + target.getClass() + "#" + method.getName());  
		                key.append(param.hashCode());  
		            }
		            key.append('-');  
		        }  
		        return key.toString();  
		    }  
		};
	}

	public class CacheKeyGenerator implements KeyGenerator {
		public Object generate(Object o, Method method, Object... objects) {
			StringBuilder sb = new StringBuilder();
			sb.append(o.getClass().getName());
			sb.append(method.getName());
			for (Object obj : objects) {
				sb.append(""+obj);
			}
			System.out.println("keyGenerator=" + sb.toString());
			return sb.toString();
		}
	}
	
	
	@Bean
	@Override
	public CacheManager cacheManager() {
		CacheManager cacheManager = new RedisCacheManager(beiniRedisTemplate(getRedisConnectionFactory()));
		return cacheManager;
	}

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

	/*@Bean
	public BeiniSetRedisUtil getBeiniSetRedisUtil() {
		return new BeiniSetRedisUtil();
	}*/

	@Bean
	public BeiniAllRedisUtil getBeiniAllRedisUtil() {
		return new BeiniAllRedisUtil();
	}

	@Bean("beiniRedisTemplate")
	public RedisTemplate<?, ?> beiniRedisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		// key序列化方式;但是如果方法上有Long等非String类型的话，会报类型转换错误；
		
		/*该方法造成后的值不能被重用*/
		//RedisSerializer<?> redisSerializer = new GenericFastJsonRedisSerializer();
		
		/*key中不能含有Long、Integer的类型值*/
		//RedisSerializer<?> redisSerializer = new StringRedisSerializer();
		RedisSerializer<?> redisSerializer = new GenericJackson2JsonRedisSerializer();
		//RedisSerializer<?> redisSerializer = new JdkSerializationRedisSerializer();
		redisTemplate.setDefaultSerializer(redisSerializer);
		redisTemplate.setKeySerializer(redisSerializer);
		redisTemplate.setHashKeySerializer(redisSerializer);
		/* JdkSerializationRedisSerializer序列化方式,也是spring-data-redis的默认序列化方式; */
		// JdkSerializationRedisSerializer jdkRedisSerializer = new
		// JdkSerializationRedisSerializer();
		// redisTemplate.setValueSerializer(jdkRedisSerializer);
		// redisTemplate.setHashValueSerializer(jdkRedisSerializer);
		//redisTemplate.afterPropertiesSet();
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
