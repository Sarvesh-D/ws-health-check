package com.ds.ws.health.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;


@org.springframework.stereotype.Service
public class NearRealTimeEnvDetails extends FetchDetailsFromReport {
	
	private static final Logger logger = LoggerFactory.getLogger(NearRealTimeEnvDetails.class);

	@Autowired
	private WSHealthUtils wsHealthUtils;
	
	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;
	
	
	@Override
	int getFirstRowNum() {
		int firstRowNum = 0;
		final int totalServices = wsHealthUtils.getAllServices().size();
		int totalPings = reportUtils.getPings();
		logger.debug("Total Pings [{}]", totalPings);
		// first row is table header which corresponds to row num 1 or row index 0
		if(totalPings == 1) firstRowNum = 1;
		else firstRowNum = 1 + (--totalPings * totalServices); // adding 1 to exclude table header
		logger.debug("First Row number = [{}]", firstRowNum);
		return firstRowNum;
	}
	
	@Override
	int getLastRowNum() {
		int lastRowNum = 0;
		final int totalServices = wsHealthUtils.getAllServices().size();
		int totalPings = reportUtils.getPings();
		logger.debug("Total Pings [{}]", totalPings);
		lastRowNum = totalPings * totalServices;
		logger.debug("Last Row number = [{}]", lastRowNum);
		return lastRowNum;
	}
	
}
