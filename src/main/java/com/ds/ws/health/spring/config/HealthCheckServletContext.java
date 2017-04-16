package com.ds.ws.health.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
@ComponentScan(basePackages = "com.ds.ws.health.web")
public class HealthCheckServletContext extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
	registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/");
	registry.addResourceHandler("/html/**").addResourceLocations("/resources/html/");
	registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/");
	registry.addResourceHandler("/img/**").addResourceLocations("/resources/img/");
    }

    /*
     * @Override public void
     * extendMessageConverters(List<HttpMessageConverter<?>> converters) {
     * converters.add(new StringHttpMessageConverter()); converters.add(new
     * MappingJackson2HttpMessageConverter()); }
     */

}
