package com.barclays.solveit.ws.health.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

/**
 * ServiceTimeStatusResponse to be used as {@link ResponseEntity}
 * @author G09633463
 * @since 19/09/2016
 * @version 1.0
 *
 */
public class ServiceTimeStatusResponse {
	
	private List<ServiceTimeStatus> serviceTimes;

	public List<ServiceTimeStatus> getServiceTimes() {
		if(null == serviceTimes)
			serviceTimes = new ArrayList<>();
		return serviceTimes;
	}
	
}
