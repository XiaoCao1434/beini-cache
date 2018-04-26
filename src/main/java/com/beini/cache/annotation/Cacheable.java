package com.beini.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存设置注解 <br>
 * 方法级别的注解，建议设置在service层 <br>
 * 该注解可配合 @CacheKey 的参数级别注解参数使用
 * 
 * @author lb_chen
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Cacheable {
	/**
	 * 缓存key值生成策略，默认选择 DEFAULT
	 * 
	 * @author lb_chen
	 */
	public enum KeyMode {
		/** 只有加了@CacheKey的参数,才加入key后缀中 */
		DEFAULT,
		/** 只有基本类型参数,才加入key后缀中,如:String,Integer,Long,Short,Boolean */
		BASIC,
		/** 所有参数都加入key后缀 */
		ALL;
	}

	/** 缓存key */
	public String key() default "";

	/** key的后缀模式 */
	public KeyMode keyMode() default KeyMode.DEFAULT;
	
	/** 返回值类的class<br>必填属性<br>&nbsp;&nbsp;如 ：String.class*/
	public Class<?> clazz() ;

	/** 缓存多少秒,默认为0,无限期 */
	public int expire() default 0;
}