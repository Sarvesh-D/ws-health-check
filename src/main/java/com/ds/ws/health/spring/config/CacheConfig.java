package com.ds.ws.health.spring.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.ds.ws.health.common.CustomCacheKeyGenerator;
import com.ds.ws.health.exception.HealthCheckException;

import net.sf.ehcache.CacheException;

/**
 * Cache Configuration
 * 
 * @author <a href="mailto:sarvesh.dubey@cdk.com">Sarvesh Dubey</a>
 *
 * @since 30-04-2017
 * @version 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    @Override
    public CacheManager cacheManager() {
	return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
	net.sf.ehcache.CacheManager cacheManager = null;
	try (InputStream iStream = new ClassPathResource("ehcache.xml").getInputStream()) {
	    cacheManager = net.sf.ehcache.CacheManager.create();
	} catch (CacheException | IOException e) {
	    throw new HealthCheckException("Exception occured while initialising cache manager : " + e.getMessage());
	}
	return cacheManager;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
	return new CustomCacheKeyGenerator();
    }

}
