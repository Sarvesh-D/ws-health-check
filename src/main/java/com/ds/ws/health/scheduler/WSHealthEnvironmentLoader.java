package com.barclays.solveit.ws.health.scheduler;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.barclays.solveit.ws.health.core.EnvironmentLoader;

/**
 * Scheduler class for loading environments 
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@Component
public class WSHealthEnvironmentLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(WSHealthEnvironmentLoader.class);
	
	@Autowired
	@Qualifier("defaultEnvironmentLoader")
	private EnvironmentLoader environmentLoader;
	
	@PostConstruct
	private void loadEnvironmentsOnStartup() {
		logger.debug("Loading environments on startup");
		loadEnvironments();
	}
	
	@Scheduled(cron = "${env.load.interval}")
	void loadEnvironments() {
		logger.debug("Refreshing environments");
		environmentLoader.loadEnvironments();
	}

}
