package com.ds.ws.health.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.CFRuleBase.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ds.ws.health.common.ReportConstants;
import com.ds.ws.health.exception.HealthCheckException;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.util.WorkbookUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper class for generating reports.
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@Component
@Slf4j
public class WSHealthReportGeneratorUtils {

    @Autowired
    private WorkbookUtils workbookUtils;

    @Autowired
    private ReportConstants reportConstants;

    private int rowNum = 0;

    private Workbook report;

    private Sheet sheet;

    private String[] reportHeaders;

    /**
     * Builds the report for given set of {@link Service}.<br>
     * Each Service in the list will correspond to a row in the report table.
     * 
     * @param serviceHealthDetails
     *            to be saved in the report
     */
    public void buildReport(Set<Service> serviceHealthDetails) {
	DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
	for (Service serviceDetail : serviceHealthDetails) {
	    Row row = sheet.createRow(rowNum++);
	    int cellNum = 0;
	    String time = timeFormatter.print(serviceDetail.getServiceTimeStatuses().get(serviceDetail.getServiceTimeStatuses().size()-1).getTime());
	    row.createCell(cellNum++).setCellValue(time);
	    row.createCell(cellNum++).setCellValue(serviceDetail.getEnvironment());
	    row.createCell(cellNum++).setCellValue(serviceDetail.getProvider());
	    row.createCell(cellNum++).setCellValue(serviceDetail.getDescription());
	    workbookUtils.createHyperlinkCell(row.createCell(cellNum++), serviceDetail.getUri());
	    row.createCell(cellNum).setCellValue(serviceDetail.getStatus().toString());
	    log.trace("Row entered for Service {}", serviceDetail);
	}
    }

    /**
     * Gets the report file path based on the date passed
     * 
     * @param date
     *            for which the report file path is required.
     */
    public String getReportFileForDate(LocalDate date) {
	Assert.notNull(date, "Date cannot be null");
	log.debug("Getting Report file path for date {}", date);
	return getReportFileNameFor(date);
    }

    /**
     * saves the report to the destination as specified by reportFilePath
     */
    public void saveReport() {
	try (FileOutputStream out = new FileOutputStream(getReportFileNameFor(LocalDate.now()))) {
	    log.debug("Saving report started...");
	    log.debug("Applying condtional formatting");
	    applyConditionalFormattingRule();
	    log.debug("Formatting as Table");
	    workbookUtils.formatAsTable(0, rowNum, 0, reportHeaders.length - 1);
	    log.debug("Auto Adjusting columns");
	    workbookUtils.autoAdjustColumnWidth(0, reportHeaders.length - 1);
	    insertFooter();
	    report.write(out);
	    log.debug("Report file saved...");
	} catch (IOException e) {
	    throw new HealthCheckException(String.format("Error occured while saving report : %s", e.getMessage()));
	} finally {
	    initReportFile(); // reset report file to start writing data in new
			      // file
	}
    }

    /**
     * apply conditional formatting TODO FIX ME
     */
    private void applyConditionalFormattingRule() {
	SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

	ConditionalFormattingRule successRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL,
		"GREEN");
	PatternFormatting patternFmtSuccess = successRule.createPatternFormatting();
	patternFmtSuccess.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);

	ConditionalFormattingRule failureRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL,
		"RED");
	PatternFormatting patternFmtFailure = failureRule.createPatternFormatting();
	patternFmtFailure.setFillBackgroundColor(IndexedColors.RED.index);

	CellRangeAddress[] regions = { CellRangeAddress.valueOf("F2:F" + rowNum) };

	sheetCF.addConditionalFormatting(regions, successRule, failureRule);

    }

    /**
     * @return Gets the report file path
     */
    private String getReportFileNameFor(LocalDate date) {
	final String reportFileName = reportConstants.reportFilePath + System.getProperty("file.separator")
		+ reportConstants.reportFileName + "_" + date + ".xlsx";
	log.debug("Report File Name for date {} is {}", date, reportFileName);
	return reportFileName;
    }

    /**
     * initialise important properties
     */
    @PostConstruct
    private void init() {
	reportHeaders = StringUtils.split(reportConstants.reportHeaders, ",");
	initReportFile();
    }

    /**
     * inits a new report file post file rollover
     */
    private void initReportFile() {
	log.debug("Intialising Report File..");
	rowNum = 0; // reset the sheet row number counter
	log.debug("Row Num set to {}", rowNum);
	log.debug("Reseting Workbook");
	report = workbookUtils.loadWorkbook();
	log.debug("Reseting WorkSheet");
	sheet = workbookUtils.loadWorksheet(reportConstants.reportFileSheetName);
	insertHeader();
    }

    /**
     * inserts the report footer
     */
    private void insertFooter() {
	log.debug("Inserting Footer");
	Row row = sheet.createRow(rowNum + 2);
	workbookUtils.createHyperlinkCell(row.createCell(4), reportConstants.reportFooterLink,
		reportConstants.reportFooter);
    }

    /**
     * inserts the report header
     */
    private void insertHeader() {
	log.debug("Inserting Header");
	Row row = sheet.createRow(rowNum++);
	int cellNum = 0;
	for (String title : reportHeaders) {
	    row.createCell(cellNum++).setCellValue(title);
	}
    }

}