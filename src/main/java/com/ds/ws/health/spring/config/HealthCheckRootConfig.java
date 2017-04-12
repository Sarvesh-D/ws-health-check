package com.ds.ws.health.spring.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

@Configuration
@EnableAspectJAutoProxy
@EnableScheduling
@ComponentScan(basePackages = { "com.ds.ws.health.common", "com.ds.ws.health.core", "com.ds.ws.health.service",
	"com.ds.ws.health.mail", "com.ds.ws.health.report", "com.ds.ws.health.scheduler", "com.ds.ws.health.util" })
@PropertySource(value = { "classpath:core.properties", "classpath:environments.properties",
	"classpath:report.properties", "classpath:scheduler.properties", "classpath:mail.properties" })
@Import(value = { DBConfig.class, CacheConfig.class })
public class HealthCheckRootConfig {

    @Autowired
    private Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
	return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PropertiesFactoryBean coreProperties() {
	return getPropertiesBeanForClassPathResource("core.properties");
    }

    @Bean
    public PropertiesFactoryBean environmentProperties() {
	return getPropertiesBeanForClassPathResource("environments.properties");
    }

    @Bean
    public PropertiesFactoryBean mailProperties() {
	return getPropertiesBeanForClassPathResource("mail.properties");
    }

    @Bean
    public JavaMailSenderImpl mailSender() {
	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	mailSender.setHost(env.getRequiredProperty("mail.host"));
	mailSender.setPort(Integer.valueOf(env.getRequiredProperty("mail.port")));
	mailSender.setUsername(env.getRequiredProperty("mail.username"));
	mailSender.setPassword(env.getRequiredProperty("mail.password"));

	Properties javaMailProperties = new Properties();
	javaMailProperties.setProperty("mail.smtp.auth", env.getRequiredProperty("mail.smtp.auth"));
	javaMailProperties.setProperty("mail.smtp.starttls.enable",
		env.getRequiredProperty("mail.smtp.starttls.enable"));
	javaMailProperties.setProperty("mail.debug", env.getRequiredProperty("mail.debug"));

	mailSender.setJavaMailProperties(javaMailProperties);
	return mailSender;
    }

    @Bean
    public PropertiesFactoryBean reportProperties() {
	return getPropertiesBeanForClassPathResource("report.properties");
    }

    @Bean
    public PropertiesFactoryBean schedulerProperties() {
	return getPropertiesBeanForClassPathResource("scheduler.properties");
    }

    @Bean
    public SimpleMailMessage templateMessage() {
	SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	simpleMailMessage.setFrom(env.getRequiredProperty("report.mail.from"));
	return simpleMailMessage;
    }

    @Bean
    public VelocityEngineFactoryBean velocityEngine() {
	VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
	velocityEngineFactoryBean.setResourceLoaderPath("classpath:");
	velocityEngineFactoryBean.setPreferFileSystemAccess(false);
	return velocityEngineFactoryBean;
    }

    private PropertiesFactoryBean getPropertiesBeanForClassPathResource(String resourcePath) {
	PropertiesFactoryBean bean = new PropertiesFactoryBean();
	bean.setLocation(new ClassPathResource(resourcePath));
	return bean;
    }

}
