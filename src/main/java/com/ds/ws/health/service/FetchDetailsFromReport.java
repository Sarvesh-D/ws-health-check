package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.model.Environment;
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

	List<Service> serviceDetailsFromReport = wsHealthUtils.getServiceDetailsFromReport(getReportBook(),
		getFirstRowNum(), getLastRowNum());
	final int totalServices = getExpectedRowsCount();
	if (serviceDetailsFromReport.size() != totalServices)
	    logger.error("Number of rows fetched [{}] in not equal to total service rows [{}]",
		    serviceDetailsFromReport.size(), totalServices);

	environmentDetails.stream().map(Environment::getComponents).forEach(
		providers -> wsHealthUtils.setStatusForProvidersFromReport(providers, serviceDetailsFromReport));

	logger.info("Getting Env Health Details realtime completed.");
	return Collections.unmodifiableList(environmentDetails);
    }

    /**
     * @return Reasonable default return total services count
     */
    int getExpectedRowsCount() {
	return wsHealthUtils.getAllServices().size();
    }

    abstract int getFirstRowNum();

    abstract int getLastRowNum();

    /**
     * @return current workbook by default
     */
    Workbook getReportBook() {
	return reportUtils.getReport();
    }

}
