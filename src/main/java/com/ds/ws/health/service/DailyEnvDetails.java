package com.barclays.solveit.ws.health.service;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.barclays.solveit.ws.health.common.CoreConstants;
import com.barclays.solveit.ws.health.common.ReportConstants;
import com.barclays.solveit.ws.health.report.WSHealthReportGeneratorUtils;
import com.barclays.solveit.ws.health.util.WSHealthUtils;
import com.barclays.solveit.ws.health.util.WorkbookUtils;

@org.springframework.stereotype.Service
public class DailyEnvDetails extends FetchDetailsFromReport {
	
	private static final Logger logger = LoggerFactory.getLogger(DailyEnvDetails.class);

	@Autowired
	private WSHealthUtils wsHealthUtils;
	
	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;
	
	@Autowired
	private ReportConstants reportConstants;
	
	@Autowired
	private CoreConstants coreConstants;
	
	@Autowired
	private WorkbookUtils workbookUtils;

	@Override
	int getFirstRowNum() {
		return 1;
	}
	
	@Override
	int getLastRowNum() {
		return getReportBook().getSheet(reportConstants.reportFileSheetName).getLastRowNum() - 1;
	}
	
	@Override
	Workbook getReportBook() {
		final LocalDate reportFileDate = LocalDate.now().minusDays(1);
		logger.info("Getting Env Health for date []",reportFileDate);
		Workbook reportBook = null;
		try {
			 reportBook = workbookUtils.loadWorkbook(new XSSFWorkbook(new File(reportUtils.getReportFileForDate(reportFileDate))));
			 logger.debug("Workbook for date [{}] found", reportFileDate);
		} catch (InvalidFormatException | IOException e) {
			logger.error("Error occured fetching Workbook for date [{}]", reportFileDate);
		}
		return reportBook;
	}
	
	@Override
	int getExpectedRowsCount() {
		return getDailyMaxPing() * wsHealthUtils.getAllServices().size();
	}

	private int getDailyMaxPing() {
		return (24 * 60) / coreConstants.pingIntervalInMins + 1; // adding one for server startup ping
	}

}
