package com.ds.ws.health.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
/*
 * TODO check to see if this can be deprecated, this strategy class was required
 * to make UI faster by returning the result from service report worksheet which
 * is stored in memory.
 */
public class NearRealTimeEnvDetails extends FetchDetailsFromReport {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Autowired
    private WSHealthReportGeneratorUtils reportUtils;

    @Override
    int getFirstRowNum() {
	int firstRowNum = 0;
	final int totalServices = wsHealthUtils.getAllServices().size();
	int totalPings = reportUtils.getPings();
	log.debug("Total Pings [{}]", totalPings);
	// first row is table header which corresponds to row num 1 or row index
	// 0
	if (totalPings == 1)
	    firstRowNum = 1;
	else {
	    // adding 1 to exclude table header
	    firstRowNum = 1 + (--totalPings * totalServices);
	}
	log.debug("First Row number = [{}]", firstRowNum);
	return firstRowNum;
    }

    @Override
    int getLastRowNum() {
	int lastRowNum = 0;
	final int totalServices = wsHealthUtils.getAllServices().size();
	int totalPings = reportUtils.getPings();
	log.debug("Total Pings [{}]", totalPings);
	lastRowNum = totalPings * totalServices;
	log.debug("Last Row number = [{}]", lastRowNum);
	return lastRowNum;
    }

}
