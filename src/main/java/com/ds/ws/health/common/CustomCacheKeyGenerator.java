package com.ds.ws.health.common;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

public class CustomCacheKeyGenerator implements KeyGenerator{

	@Override
	public Object generate(Object target, Method method, Object... params) {
		int key = method.toString().hashCode();
		for (Object param : params) {
			key += param.hashCode();
		}
		return key;
	}

}
