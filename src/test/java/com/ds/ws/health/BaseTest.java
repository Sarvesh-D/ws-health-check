package com.ds.ws.health;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ds.ws.health.spring.config.HealthCheckRootConfig;

public class BaseTest {

    protected static ApplicationContext rootContext;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
	rootContext = new AnnotationConfigApplicationContext(HealthCheckRootConfig.class);
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
	ConfigurableApplicationContext.class.cast(rootContext).close();
    }

}
