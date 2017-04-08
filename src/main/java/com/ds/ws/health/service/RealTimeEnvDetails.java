package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.util.WSHealthUtils;

@org.springframework.stereotype.Service
public class RealTimeEnvDetails extends EnvDetailsFetchStrategy {

    private static final Logger logger = LoggerFactory.getLogger(RealTimeEnvDetails.class);

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Override
    List<Environment> getEnvHealthDetails() {
	logger.info("Getting Env Health Details realtime");
	List<Environment> environmentDetails = new ArrayList<>(wsHealthUtils.getAllEnvironments());
	Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
	environmentDetails.stream().map(Environment::getComponents)
		.forEach(providers -> wsHealthUtils.setStatusForProviders(providers));
	logger.info("Getting Env Health Details realtime completed.");
	return Collections.unmodifiableList(environmentDetails);

    }

}
