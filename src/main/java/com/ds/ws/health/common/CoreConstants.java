package com.barclays.solveit.ws.health.common;

import java.lang.reflect.Field;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class containing core constants.<br>
 * This are application critical constants.<br>
 * <b>DO NOT</b> alter these before understanding the significance 
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@Component
public final class CoreConstants {
	
	public final String environmentDetailsSeparatorKey;
	
	public final int connectionTimeoutInMillis;
	
	public final String connectionProxyHost;

	public final int connectionProxyPort;
	
	public final int pingIntervalInMins;
	
	public final int componentStatusPingCount;
	
	private static final Logger logger = LoggerFactory.getLogger(CoreConstants.class); 
	
	@Autowired
	private CoreConstants(Properties coreProperties, Properties schedulerProperties) {
		logger.info("initialising core constants...");
		environmentDetailsSeparatorKey = coreProperties.getProperty("environment.details.separator.key");
		connectionTimeoutInMillis = Integer.parseInt(coreProperties.getProperty("connection.timeout"));
		connectionProxyHost = coreProperties.getProperty("connection.proxy.host");
		connectionProxyPort = Integer.valueOf(coreProperties.getProperty("connection.proxy.port"));
		pingIntervalInMins = Integer.valueOf(schedulerProperties.getProperty("ping.mins"));
		componentStatusPingCount = Integer.valueOf(schedulerProperties.getProperty("component.status.ping.count"));
		logger.info("initialising core constants completed");
		
		for (Field field : CoreConstants.class.getDeclaredFields()) {
			try {
				logger.debug("Setting constant [{}] --> [{}]", field.getName(), field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
}
