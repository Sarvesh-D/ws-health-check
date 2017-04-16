package com.ds.ws.health.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import lombok.ToString;

/**
 * ServiceTimeStatusResponse to be used as {@link ResponseEntity}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 19/09/2016
 * @version 1.0
 *
 */
@ToString(of = "serviceTimes")
public class ServiceTimeStatusResponse {

    private List<ServiceTimeStatus> serviceTimes;

    public List<ServiceTimeStatus> getServiceTimes() {
	if (null == serviceTimes)
	    serviceTimes = new ArrayList<>();
	return serviceTimes;
    }

}
