package com.barclays.solveit.ws.health.common;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class CoreConstants {
	
	public final String servicesKey;

	public final String servicesSeparatorKey;

	public final String serviceDetailsSeparatorKey;
	
	public final int connectionTimeoutInMillis;
	
	public final String serviceStatusPassed;
	
	public final String serviceStatusFailed;

	@Autowired
	private CoreConstants(Properties coreProperties) {
		servicesKey = coreProperties.getProperty("services.key");
		servicesSeparatorKey = coreProperties.getProperty("services.separator.key");
		serviceDetailsSeparatorKey = coreProperties.getProperty("service.details.separator.key");
		connectionTimeoutInMillis = Integer.parseInt(coreProperties.getProperty("connection.timeout"));
		serviceStatusPassed = coreProperties.getProperty("service.status.passed");
		serviceStatusFailed = coreProperties.getProperty("service.status.failed");
	}
	
}
