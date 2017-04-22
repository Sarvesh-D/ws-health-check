package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.Service.ServiceStatus;
import com.ds.ws.health.model.ServiceTimeStatus;
import com.ds.ws.health.util.WSHealthUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation for {@link WSHealthService}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@org.springframework.stereotype.Service("wSHealthServiceImpl")
@Slf4j
public class WSHealthServiceImpl implements WSHealthService {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Override
    public List<Environment> getEnvHealthDetails(EnvDetailsFetchMode fetchMode) {
	return WSHealthUtils.instanceOf(fetchMode.getStrategy()).getEnvHealthDetails();
    }

    @Override
    public List<ServiceTimeStatus> getReportForService(Service service) {
	Service loadedService = wsHealthUtils.getLoadedService(service);
	return loadedService.getServiceTimeStatusResponse().getServiceTimes();
    }

    @Override
    public List<Service> getServiceHealthDetails() {
	log.info("Getting service health details started...");
	List<Service> serviceDetails = new ArrayList<>(wsHealthUtils.getAllServices());
	Collections.sort(serviceDetails, Service.SERVICE_DETAIL_COMPARATOR);
	for (Service serviceDetail : serviceDetails) {
	    ServiceStatus status = wsHealthUtils.getStatusForService(serviceDetail);
	    // set below entry when scheduler pings the service to test
	    serviceDetail.getServiceTimeStatusResponse().getServiceTimes()
		    .add(new ServiceTimeStatus(System.currentTimeMillis(), status));
	    // calculate overall status of service after each ping
	    serviceDetail.setStatus(status).calculateOverallStatus();
	}
	log.info("Getting service health details completed");
	return Collections.unmodifiableList(serviceDetails);
    }

}