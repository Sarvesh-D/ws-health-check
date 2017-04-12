package com.ds.ws.health.spring.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.ds.ws.health.common.CustomCacheKeyGenerator;

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
	EhCacheManagerFactoryBean cacheManager = new EhCacheManagerFactoryBean();
	cacheManager.setCacheManagerName("springCache");
	cacheManager.setConfigLocation(new ClassPathResource("ehcache.xml"));
	return cacheManager.getObject();
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
	return new CustomCacheKeyGenerator();
    }

}
