package com.ds.ws.health.service;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.common.ReportConstants;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;
import com.ds.ws.health.util.WorkbookUtils;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
/*
 * TODO check to see if this can be deprecated, since this strategy class was
 * required to calculate the daily status of Provider. But now, the overall
 * status of provider is much more reliable which is calculated after each
 * scheduled ping to each service.
 */
public class DailyEnvDetails extends FetchDetailsFromReport {

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
    int getExpectedRowsCount() {
	return getDailyMaxPing() * wsHealthUtils.getAllServices().size();
    }

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
	log.info("Getting Env Health for date [{}]", reportFileDate);
	Workbook reportBook = null;
	try {
	    reportBook = workbookUtils
		    .loadWorkbook(new XSSFWorkbook(new File(reportUtils.getReportFileForDate(reportFileDate))));
	    log.debug("Workbook for date [{}] found", reportFileDate);
	} catch (InvalidFormatException | IOException e) {
	    log.error("Error occured fetching Workbook for date [{}]", reportFileDate);
	}
	return reportBook;
    }

    private int getDailyMaxPing() {
	// adding one for server ping at startup
	return (24 * 60) / coreConstants.pingIntervalInMins + 1;
    }

}
