package com.ds.ws.health.service;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.util.WSHealthUtils;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class RealTimeEnvDetails extends EnvDetailsFetchStrategy {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Override
    Set<Environment> getEnvHealthDetails() {
	log.info("Getting Env Health Details realtime");
	Set<Environment> environmentDetails = new TreeSet<>(Environment.ENVIRONMENT_NAME_COMPARATOR);
	environmentDetails.addAll(wsHealthUtils.getAllEnvironments());
	environmentDetails.stream().map(Environment::getComponents).flatMap(Set::stream).map(Provider::getServices)
		.flatMap(Set::stream).forEach(this::setStatusForService);
	log.info("Getting Env Health Details realtime completed.");
	return Collections.unmodifiableSet(environmentDetails);

    }

    private void setStatusForService(Service service) {
	service.setStatus(wsHealthUtils.getStatusForService(service));
    }

}
