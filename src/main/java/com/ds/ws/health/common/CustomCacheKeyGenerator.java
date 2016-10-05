package com.barclays.solveit.ws.health.common;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

/** Custom Cache key generator for Spring's ehCache
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
public class CustomCacheKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		int key = method.toString().hashCode();
		for (Object param : params) {
			key += param.hashCode();
		}
		return key;
	}

}
