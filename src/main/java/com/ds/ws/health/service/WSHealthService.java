package com.barclays.solveit.ws.health.service;

import java.util.List;

import com.barclays.solveit.ws.health.model.Environment;
import com.barclays.solveit.ws.health.model.Provider;
import com.barclays.solveit.ws.health.model.Service;
import com.barclays.solveit.ws.health.model.ServiceTimeStatus;

/**
 * Service Interface for WS Health Reports 
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 * @see WSHealthServiceImpl
 */
public interface WSHealthService {
	
	/**
	 * This method returns the List of {@link Service} after pinging them to get their status
	 * @return List of {@link Service}
	 */
	List<Service> getServiceHealthDetails();
	
	/**
	 * This method returns list of {@link Environment} after pinging each {@link Service} of each {@link Provider}.<br>
	 * This is the realtime version of {@link WSHealthService#getEnvHealthDetailsFromReport()}
	 * @return List of {@link Environment}
	 */
	List<Environment> getEnvHealthDetails(EnvDetailsFetchMode fetchMode);
	
	/**
	 * This method returns list of {@link ServiceTimeStatus} containing Time and Status for the given service from report.
	 * @param service whose all time/status is to be fetched 
	 * @return List
	 */
	List<ServiceTimeStatus> getReportForService(Service service);

}
