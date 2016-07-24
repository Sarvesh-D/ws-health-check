package com.barclays.solveit.ws.health.scheduler;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class SchedulerConstants {
	
	public final String pingInterval;
	
	public final String fileRollOverInterval;
	
	@Autowired
	private SchedulerConstants(Properties schedulerProperties) {
		pingInterval = schedulerProperties.getProperty("ping.interval");
		fileRollOverInterval = schedulerProperties.getProperty("file.rollover.interval");
	}

}
