package com.ds.ws.health.common;

import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class containing core constants.<br>
 * This are application critical constants.<br>
 * <b>DO NOT</b> alter these before understanding the significance
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
public final class CoreConstants {

    private static final Logger logger = LoggerFactory.getLogger(CoreConstants.class);

    public final String serviceDetailsSeparatorKey;

    public final int connectionTimeoutInMillis;

    public final String connectionProxyHost;

    public final int connectionProxyPort;

    public final int pingIntervalInMins;

    public final int componentStatusPingCount;

    @Autowired
    private CoreConstants(Properties coreProperties, Properties schedulerProperties) {
	logger.info("initialising core constants...");
	serviceDetailsSeparatorKey = coreProperties.getProperty("service.details.separator.key");
	connectionTimeoutInMillis = Integer.parseInt(coreProperties.getProperty("connection.timeout"));
	connectionProxyHost = coreProperties.getProperty("connection.proxy.host");
	connectionProxyPort = Integer.valueOf(coreProperties.getProperty("connection.proxy.port"));
	pingIntervalInMins = Integer.valueOf(schedulerProperties.getProperty("ping.mins"));
	componentStatusPingCount = Integer.valueOf(schedulerProperties.getProperty("component.status.ping.count"));
	logger.info("initialising core constants completed");

	Arrays.stream(CoreConstants.class.getDeclaredFields()).forEach(field -> {
	    try {
		logger.debug("Setting constant [{}] --> [{}]", field.getName(), field.get(this));
	    } catch (IllegalArgumentException | IllegalAccessException e1) {
		e1.printStackTrace();
	    }
	});
    }

}
