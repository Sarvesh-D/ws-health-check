package com.ds.ws.health.scheduler;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.ws.health.core.EnvironmentLoader;

/**
 * Scheduler class for loading environments
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
public class WSHealthEnvironmentLoader {

    private static final Logger logger = LoggerFactory.getLogger(WSHealthEnvironmentLoader.class);

    @Autowired
    @Qualifier("defaultEnvironmentLoader")
    private EnvironmentLoader environmentLoader;

    @Scheduled(cron = "${env.load.interval}")
    void loadEnvironments() {
	logger.debug("Refreshing environments");
	environmentLoader.loadEnvironments();
    }

    @PostConstruct
    private void loadEnvironmentsOnStartup() {
	logger.debug("Loading environments on startup");
	loadEnvironments();
    }

}
