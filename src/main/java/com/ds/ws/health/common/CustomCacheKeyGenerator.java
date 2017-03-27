package com.ds.ws.health.common;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.cache.interceptor.KeyGenerator;

/**
 * Custom Cache key generator for Spring's ehCache
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
public class CustomCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
	int key = method.toString().hashCode();
	key += Arrays.stream(params).map(Object::hashCode).collect(Collectors.counting()).intValue();
	return key;
    }

}
