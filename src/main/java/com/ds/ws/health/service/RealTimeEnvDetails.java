package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.util.WSHealthUtils;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class RealTimeEnvDetails extends EnvDetailsFetchStrategy {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Override
    List<Environment> getEnvHealthDetails() {
	log.info("Getting Env Health Details realtime");
	List<Environment> environmentDetails = new ArrayList<>(wsHealthUtils.getAllEnvironments());
	Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
	environmentDetails.stream().map(Environment::getComponents)
		.forEach(providers -> wsHealthUtils.setStatusForProviders(providers));
	log.info("Getting Env Health Details realtime completed.");
	return Collections.unmodifiableList(environmentDetails);

    }

}
