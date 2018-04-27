package com.beini.cache.aop;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beini.cache.annotation.CacheKey;
import com.beini.cache.annotation.Cacheable;
import com.beini.cache.annotation.Cacheable.KeyMode;
import com.beini.cache.utils.BeiniStringRedisUtil;
import com.beini.core.utils.JsonUtil;

/**
 * 缓存环绕通知处理类
 * @author lb_chen
 */
@SuppressWarnings("deprecation")
@Aspect
@Component
public class CacheableAop {
	/** key值拼接分割符 */
	private static final String SEPARATOR = ".";
	@Autowired
	BeiniStringRedisUtil beiniStringRedisUtil;

	/**
	 * 基本的切面流程如下:<br>
	 * <ol>
	 * <li>先根据业务取KEY值</li>
	 * <li>尝试从缓存中取值</li>
	 * <li>如果缓存中有数据，则直接返回业务类，业务类也直接返回（以下过程将不执行）</li>
	 * <li>如果缓存中没有数据，则直接到业务类中取数据</li>
	 * <li>如果注解中没有设置过期时间，则将其数据设置为永久缓存</li>
	 * <li>如果注解中有设置过期时间，否则将其值按照注解配置设置过期时间和值</li>
	 * </ol>
	 * 
	 * @param pjp
	 *            ProceedingJoinPoint
	 * @param cache
	 *            cache
	 * @return 缓存的数据或者业务端获取的数据
	 * @throws Throwable
	 */
	@Around("@annotation(cache)")
	public Object cached(final ProceedingJoinPoint pjp, Cacheable cache) throws Throwable {
		String key = getCacheKey(pjp, cache);
		//BeiniStringRedisUtil beiniStringRedisUtil= new BeiniStringRedisUtil();
		/* 从缓存获取数据 */
		Object value = JsonUtil.stringToBean(""+beiniStringRedisUtil.get(key), cache.clazz());
		if (value != null) {
			/* 如果有数据,则直接返回 */
			return value;
		}
		/* 跳过缓存,到后端查询数据 */
		value = pjp.proceed();
		if (value != null) {
			/* 如果没有设置过期时间,则无限期缓存 */
			if (cache.expire() <= 0) {
				beiniStringRedisUtil.set(key, JsonUtil.beanToString(value));
			} /* 否则设置缓存时间 */
			else {
				beiniStringRedisUtil.set(key, JsonUtil.beanToString(value), cache.expire(), TimeUnit.SECONDS);
			}
		}
		return value;
	}

	/**
	 * 获取缓存的key值 <br>
	 * 若为名称相同的方法配置缓存,可以在@Cacheable中加入key属性,追加额外的key后缀,现在支持如下三种策略生成机制：
	 * <ul>
	 * <li>DEFAULT:表示只有加了@CacheKey的参数才能追加到key后缀</li>
	 * <li>BASIC:自动将基本类型追加到key后缀,而无需再配置@CacheKey</li>
	 * <li>ALL:自动将所有参数追加到lkey后缀,而无需再配置@CacheKey</li>
	 * </ul>
	 * 
	 * @param pjp
	 *            ProceedingJoinPoint
	 * @param cache
	 *            cache
	 * @return 缓存的KEY值
	 */
	private String getCacheKey(ProceedingJoinPoint pjp, Cacheable cache) {
		StringBuilder buf = new StringBuilder();
		/* 设置key值的前半部分：(完整类路径+方法名) */
		buf.append(pjp.getSignature().getDeclaringTypeName()).append(SEPARATOR).append(pjp.getSignature().getName());
		if (cache.key().length() > 0) {
			buf.append(SEPARATOR).append(cache.key());
		}
		/* 设置key值的后半部分：根据cacheKey的注解来标注 */
		Object[] args = pjp.getArgs();
		/* DEFAULT:表示只有加了@CacheKey的参数才能追加到key后缀 */
		if (cache.keyMode() == KeyMode.DEFAULT) {
			Annotation[][] pas = ((MethodSignature) pjp.getSignature()).getMethod().getParameterAnnotations();
			for (int i = 0; i < pas.length; i++) {
				for (Annotation an : pas[i]) {
					if (an instanceof CacheKey) {
						buf.append(SEPARATOR).append(args[i].toString());
						break;
					}
				}
			}
		}
		/* BASIC:自动将基本类型追加到key后缀,而无需再配置@CacheKey */
		else if (cache.keyMode() == KeyMode.BASIC) {
			for (Object arg : args) {
				/* 常见字符串应用类型 */
				if (arg instanceof String) {
					buf.append(SEPARATOR).append(arg);
				}
				/* 常见整形类型 */
				else if (arg instanceof Integer || arg instanceof Long || arg instanceof Short || arg instanceof Byte) {
					buf.append(SEPARATOR).append(arg.toString());
				}
				/* 常见布尔类型 */
				else if (arg instanceof Boolean) {
					buf.append(SEPARATOR).append(arg.toString());
				}
				/* 常见的浮点数类型 */
				else if (arg instanceof Float || arg instanceof Double) {
					buf.append(SEPARATOR).append(arg.toString());
				}
			}
		}
		/* ALL:自动将所有参数追加到lkey后缀,而无需再配置@CacheKey */
		else if (cache.keyMode() == KeyMode.ALL) {
			for (Object arg : args) {
				buf.append(SEPARATOR).append(arg.toString());
			}
		}
		return buf.toString();
	}
}