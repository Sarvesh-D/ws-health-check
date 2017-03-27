package com.ds.ws.health.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;

@org.springframework.stereotype.Service
public class HourlyEnvDetails extends FetchDetailsFromReport {

    private static final Logger logger = LoggerFactory.getLogger(HourlyEnvDetails.class);

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
	int totalPings = (reportUtils.getPings() - 1) - getPingsForStatusCalc(); // subtracting
										 // the
										 // server
										 // startup
										 // ping
	logger.debug("Total Pings [{}]", totalPings);
	if (totalPings <= 0)
	    firstRowNum = 1; // 0 is table header, starting from row 1
	else
	    firstRowNum = 1 + (totalPings * wsHealthUtils.getAllServices().size());
	logger.debug("First Row number = [{}]", firstRowNum);
	return firstRowNum;
    }

    @Override
    int getLastRowNum() {
	int lastRowNum = 0;
	final int totalServices = wsHealthUtils.getAllServices().size();
	int totalPings = reportUtils.getPings() - 1; // subtracting the server
						     // startup ping
	logger.debug("Total Pings [{}]", totalPings);
	lastRowNum = totalPings * totalServices;
	logger.debug("Last Row number = [{}]", lastRowNum);
	return lastRowNum;
    }

    private int getPingsForStatusCalc() {
	return coreConstants.componentStatusPingCount;
    }

}
