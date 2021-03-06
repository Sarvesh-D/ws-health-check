package com.ds.ws.health.service;

import java.util.List;
import java.util.Set;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.ServiceTimeStatus;

/**
 * Service Interface for WS Health Reports
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 * @see MonitoringServiceImpl
 */
public interface MonitoringService {

    /**
     * This method returns list of {@link Environment} after pinging each
     * {@link Service} of each {@link Provider}.<br>
     * This is the realtime version of
     * {@link MonitoringService#getEnvHealthDetailsFromReport()}
     * 
     * @return Set of {@link Environment}
     */
    Set<Environment> getEnvHealthDetails(EnvDetailsFetchMode fetchMode);

    /**
     * This method returns list of {@link ServiceTimeStatus} containing Time and
     * Status for the given service from report.
     * 
     * @param service
     *            whose all time/status is to be fetched
     * @return List
     */
    List<ServiceTimeStatus> getReportForService(Service service);

    /**
     * This method returns the List of {@link Service} after pinging them to get
     * their status
     * 
     * @return List of {@link Service}
     */
    Set<Service> getServiceHealthDetails();

}
