package com.beini.cache.utils;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.beini.BeiniCacheApplication;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=BeiniCacheApplication.class)
@Slf4j
public class BeiniAllRedisUtilTest {
	@Resource(name="beiniRedisUtil")
	private BeiniAllRedisUtil redisUtil = new BeiniAllRedisUtil();
	
	private String key="lkey";
	private String value1="lvalue4";
	private String value2="lvalue5";
	private String value3="lvalue6";
	@Test
	public void lPushTest() {
		boolean result = false;
		result = redisUtil.lPush(key, value1);
		log.info("lkey1 ： "+result);
		result = redisUtil.lPush(key, value2);
		log.info("lkey2 ： "+result);
		result = redisUtil.lPush(key, value3);
		log.info("lkey3 ： "+result);
	}
	
	@Test
	public void rPopTest() {
		Object result = null;
		result = redisUtil.rPop(key);
		log.info("rPop ： "+result);
		result = redisUtil.rPop(key);
		log.info("rPop ： "+result);
	}
}
