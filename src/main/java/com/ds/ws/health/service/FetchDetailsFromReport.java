package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;


@org.springframework.stereotype.Service
public abstract class FetchDetailsFromReport extends EnvDetailsFetchStrategy {
	
	private static final Logger logger = LoggerFactory.getLogger(NearRealTimeEnvDetails.class);

	@Autowired
	private WSHealthUtils wsHealthUtils;
	
	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;

	@Override
	List<Environment> getEnvHealthDetails() {
		logger.info("Getting Env Health near realtime");
		List<Environment> environmentDetails = new ArrayList<>(wsHealthUtils.getAllEnvironments());
		Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
		
		List<Service> serviceDetailsFromReport = wsHealthUtils.getServiceDetailsFromReport(getReportBook(), getFirstRowNum(), getLastRowNum());
		final int totalServices = getExpectedRowsCount();
		if(serviceDetailsFromReport.size() != totalServices)
			logger.error("Number of rows fetched [{}] in not equal to total service rows [{}]",serviceDetailsFromReport.size(),totalServices);
		
		Map<Provider, List<Set<Service>>> providerServiceHealthDetails = wsHealthUtils
				.getServicesHealthAllComponent(serviceDetailsFromReport);
		
		for (Environment environmentDetail : environmentDetails) {
			for (Provider component : environmentDetail.getComponents()) {
				for (Service serviceDetail : component.getServices()) {
					for (Service serviceDetailFromReport : serviceDetailsFromReport) {
						if(serviceDetail.equals(serviceDetailFromReport)) {
							serviceDetail.setStatus(serviceDetailFromReport.getStatus());
							break;
						}
					}
				}
				// TODO CHECK if component.getDownServices() CAN be used in mail_template.vm 
				// since serviceDetailsFromReport is a List, converting it to Set will cause overriding of previous data.
				// the data in the Set will correspond result of latest ping details.
				// This is fine for sending data to UI, but since mail contains overall status of the day, this will
				// lead to false results.
				component.setDownServices(serviceDetailsFromReport);
				component.setStatus(providerServiceHealthDetails.get(component));
			}
		}
		logger.info("Getting Env Health Details realtime completed.");
		return Collections.unmodifiableList(environmentDetails);
	}
	
	abstract int getFirstRowNum();
	
	abstract int getLastRowNum();
	
	/**
	 * @return Reasonable default return total services count
	 */
	int getExpectedRowsCount() {
		return wsHealthUtils.getAllServices().size();
	}
	
	/**
	 * @return current workbook by default
	 */
	Workbook getReportBook() {
		return reportUtils.getReport();
	}

}
