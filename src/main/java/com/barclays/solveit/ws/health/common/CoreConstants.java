package com.barclays.solveit.ws.health.common;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class CoreConstants {
	
	public final String environmentsKey;

	public final String environmentsSeparatorKey;

	public final String environmentDetailsSeparatorKey;
	
	public final int connectionTimeoutInMillis;
	
	public final String connectionProxyHost;

	public final int connectionProxyPort;
	
	public final String serviceStatusPassed;
	
	public final String serviceStatusFailed;
	
	@Autowired
	private CoreConstants(Properties coreProperties) {
		environmentsKey = coreProperties.getProperty("environments.key");
		environmentsSeparatorKey = coreProperties.getProperty("environments.separator.key");
		environmentDetailsSeparatorKey = coreProperties.getProperty("environment.details.separator.key");
		connectionTimeoutInMillis = Integer.parseInt(coreProperties.getProperty("connection.timeout"));
		serviceStatusPassed = coreProperties.getProperty("service.passed.key");
		serviceStatusFailed = coreProperties.getProperty("service.failed.key");
		connectionProxyHost = coreProperties.getProperty("connection.proxy.host");
		connectionProxyPort = Integer.valueOf(coreProperties.getProperty("connection.proxy.port"));
	}
	
}
