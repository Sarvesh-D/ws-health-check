package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public abstract class FetchDetailsFromReport extends EnvDetailsFetchStrategy {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Autowired
    private WSHealthReportGeneratorUtils reportUtils;

    @Override
    List<Environment> getEnvHealthDetails() {
	log.info("Getting Env Health near realtime");
	List<Environment> environmentDetails = new ArrayList<>(wsHealthUtils.getAllEnvironments());
	Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);

	List<Service> serviceDetailsFromReport = wsHealthUtils.getServiceDetailsFromReport(getReportBook(),
		getFirstRowNum(), getLastRowNum());
	final int totalServices = getExpectedRowsCount();
	if (serviceDetailsFromReport.size() != totalServices)
	    log.error("Number of rows fetched [{}] in not equal to total service rows [{}]",
		    serviceDetailsFromReport.size(), totalServices);

	/*
	 * environmentDetails.stream().map(Environment::getComponents).forEach(
	 * providers -> wsHealthUtils.setStatusForProvidersFromReport(providers,
	 * serviceDetailsFromReport));
	 */

	log.info("Getting Env Health Details realtime completed.");
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
