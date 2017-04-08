package com.ds.ws.health.scheduler;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.ws.health.core.EnvironmentLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler class for loading environments
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
@Slf4j
public class WSHealthEnvironmentLoader {

    @Autowired
    @Qualifier("defaultEnvironmentLoader")
    private EnvironmentLoader environmentLoader;

    @Scheduled(cron = "${env.load.interval}")
    void loadEnvironments() {
	log.debug("Refreshing environments");
	environmentLoader.loadEnvironments();
    }

    @PostConstruct
    private void loadEnvironmentsOnStartup() {
	log.debug("Loading environments on startup");
	loadEnvironments();
    }

}
