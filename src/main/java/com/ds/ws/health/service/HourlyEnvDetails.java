package com.ds.ws.health.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class HourlyEnvDetails extends FetchDetailsFromReport {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Autowired
    private WSHealthReportGeneratorUtils reportUtils;

    @Autowired
    private CoreConstants coreConstants;

    @Override
    int getExpectedRowsCount() {
	return getPingsForStatusCalc() * wsHealthUtils.getAllServices().size();
    }

    @Override
    int getFirstRowNum() {
	int firstRowNum = 0;
	// subtracting the server startup ping
	int totalPings = (reportUtils.getPings() - 1) - getPingsForStatusCalc();
	log.debug("Total Pings [{}]", totalPings);
	if (totalPings <= 0)
	    firstRowNum = 1; // 0 is table header, starting from row 1
	else
	    firstRowNum = 1 + (totalPings * wsHealthUtils.getAllServices().size());
	log.debug("First Row number = [{}]", firstRowNum);
	return firstRowNum;
    }

    @Override
    int getLastRowNum() {
	int lastRowNum = 0;
	final int totalServices = wsHealthUtils.getAllServices().size();
	// subtracting the server startup ping
	int totalPings = reportUtils.getPings() - 1;
	log.debug("Total Pings [{}]", totalPings);
	lastRowNum = totalPings * totalServices;
	log.debug("Last Row number = [{}]", lastRowNum);
	return lastRowNum;
    }

    private int getPingsForStatusCalc() {
	return coreConstants.componentStatusPingCount;
    }

}
