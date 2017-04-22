package com.ds.ws.health.common;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ds.ws.health.exception.HealthCheckException;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public final class CoreConstants {

    public final String serviceDetailsSeparatorKey;

    public final int connectionTimeoutInMillis;

    public final int pingIntervalInMins;

    public final int componentStatusPingCount;

    @Autowired
    private CoreConstants(Properties coreProperties, Properties schedulerProperties) {
	log.info("initialising core constants...");
	serviceDetailsSeparatorKey = coreProperties.getProperty("service.details.separator.key");
	connectionTimeoutInMillis = Integer.parseInt(coreProperties.getProperty("connection.timeout"));
	pingIntervalInMins = Integer.valueOf(schedulerProperties.getProperty("ping.mins"));
	componentStatusPingCount = Integer.valueOf(schedulerProperties.getProperty("component.status.ping.count"));
	log.info("initialising core constants completed");

	Arrays.stream(CoreConstants.class.getDeclaredFields()).forEach(field -> {
	    try {
		log.debug("Setting Core constant [{}] --> [{}]", field.getName(), field.get(this));
	    } catch (IllegalArgumentException | IllegalAccessException e1) {
		throw new HealthCheckException(
			String.format("Exception occured while setting core constants : %s", e1.getMessage()));
	    }
	});
    }

}
