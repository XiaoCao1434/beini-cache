package com.beini.cache.utils.redis.key;

import com.beini.cache.utils.redis.BasePrefix;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
	}
	public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
