package com.barclays.solveit.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.barclays.solveit.ws.health.common.CoreConstants;
import com.barclays.solveit.ws.health.model.Environment;
import com.barclays.solveit.ws.health.model.Provider;
import com.barclays.solveit.ws.health.model.Service;
import com.barclays.solveit.ws.health.model.Service.Status;
import com.barclays.solveit.ws.health.util.WSHealthUtils;

@org.springframework.stereotype.Service
public class RealTimeEnvDetails extends EnvDetailsFetchStrategy {
	
	private static final Logger logger = LoggerFactory.getLogger(RealTimeEnvDetails.class);
	
	@Autowired
	private WSHealthUtils wsHealthUtils;

	@Autowired
	private CoreConstants coreConstants;

	@Override
	List<Environment> getEnvHealthDetails() {
		logger.info("Getting Env Health Details realtime");
		List<Environment> environmentDetails = new ArrayList<>(wsHealthUtils.getAllEnvironments());
		Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
		for (Environment environmentDetail : environmentDetails) {
			for (Provider component : environmentDetail.getComponents()) {
				for (Service serviceDetail : component.getServices()) {
					logger.debug("Getting status for service {}",serviceDetail);
					Status status = wsHealthUtils.pingURL(serviceDetail.getUri(), coreConstants.connectionTimeoutInMillis)
							? Status.UP : Status.DOWN;
					serviceDetail.setStatus(status);
					logger.debug("Service is {}",status);
				}
				component.setDownServices(new ArrayList<>(component.getServices()));
				component.setStatus(wsHealthUtils.getServicesHealthForProvider(new ArrayList<>(component.getServices()), component));
			}
		}
		logger.info("Getting Env Health Details realtime completed.");
		return Collections.unmodifiableList(environmentDetails);
	
	}

}
