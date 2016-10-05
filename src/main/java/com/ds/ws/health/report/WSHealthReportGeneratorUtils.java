package com.barclays.solveit.ws.health.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.barclays.solveit.ws.health.common.ReportConstants;
import com.barclays.solveit.ws.health.model.Service;
import com.barclays.solveit.ws.health.util.WorkbookUtils;

/**
 * Helper class for generating reports.
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 *
 */
@Component 
public class WSHealthReportGeneratorUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(WSHealthReportGeneratorUtils.class);

	@Autowired
	private WorkbookUtils workbookUtils;

	@Autowired
	private ReportConstants reportConstants;

	private int rowNum = 0;

	private Workbook report;

	private Sheet sheet;

	private int pings = 0;
	
	private String[] reportHeaders;
	
	/**
	 * initialise important properties
	 */
	@PostConstruct
	private void init() {
		reportHeaders = StringUtils.split(reportConstants.reportHeaders, ",");
		initReportFile();
	}
	
	/**
	 * Builds the report for given list {@link Service}.<br>
	 * Each Service in the list will correspond to a row in the report table.
	 * @param serviceHealthDetails to be saved in the report
	 */
	public void buildReport(List<Service> serviceHealthDetails) {
		pings++; // increment ping
		for (Service serviceDetail : serviceHealthDetails) {
			Row row = sheet.createRow(rowNum++);
			int cellNum = 0;
			// TODO save provider version column in report
			row.createCell(cellNum++).setCellValue(LocalTime.now().toString());
			row.createCell(cellNum++).setCellValue(serviceDetail.getEnvironment());
			row.createCell(cellNum++).setCellValue(serviceDetail.getProvider());
			row.createCell(cellNum++).setCellValue(serviceDetail.getDescription());
			workbookUtils.createHyperlinkCell(row.createCell(cellNum++), serviceDetail.getUri());
			row.createCell(cellNum).setCellValue(serviceDetail.getStatus().toString());
			logger.debug("Row entered for Service {}", serviceDetail);
		}
	}

	/**
	 * inserts the report header
	 */
	private void insertHeader() {
		logger.debug("Inserting Header");
		Row row = sheet.createRow(rowNum++);
		int cellNum = 0;
		for (String title : reportHeaders) {
			row.createCell(cellNum++).setCellValue(title);
		}
	}
	
	/**
	 * inserts the report footer
	 */
	private void insertFooter() {
		logger.debug("Inserting Footer");
		Row row = sheet.createRow(rowNum + 2);
		workbookUtils.createHyperlinkCell(row.createCell(4), reportConstants.reportFooterLink,
				reportConstants.reportFooter);
	}

	/**
	 * saves the report to the destination as specified by reportFilePath
	 */
	public void saveReport() {
		try(FileOutputStream out = new FileOutputStream(getReportFileNameFor(LocalDate.now()))) {
			logger.debug("Saving report started...");
			logger.debug("Applying condtional formatting");
			applyConditionalFormattingRule();
			logger.debug("Formatting as Table");
			workbookUtils.formatAsTable(0,rowNum,0,reportHeaders.length-1);
			logger.debug("Auto Adjusting columns");
			workbookUtils.autoAdjustColumnWidth(0, reportHeaders.length-1);
			insertFooter();
			report.write(out);
		} catch (IOException e) {
			logger.error("Error occured while sending mail : {}",e.getMessage());
		} finally {
			pings = 0; // reset pings
			initReportFile(); // reset report file to start writing data in new file
		}
	}
	
	/**
	 * @return number of time the services have been pinged to check status
	 */
	public int getPings() {
		return pings;
	}
	
	/**
	 * apply conditional formatting
	 * TODO FIX ME 
	 */
	private void applyConditionalFormattingRule() {
		SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
		
		ConditionalFormattingRule successRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL,"UP");
		PatternFormatting patternFmtSuccess = successRule.createPatternFormatting();
		patternFmtSuccess.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
		
		ConditionalFormattingRule failureRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL,"DOWN");
		PatternFormatting patternFmtFailure = failureRule.createPatternFormatting();
		patternFmtFailure.setFillBackgroundColor(IndexedColors.RED.index);

		CellRangeAddress[] regions = {CellRangeAddress.valueOf("F2:F"+rowNum)};
		
		sheetCF.addConditionalFormatting(regions, successRule, failureRule);

	}
	
	/**
	 * inits a new report file post file rollover
	 */
	private void initReportFile() {
		logger.debug("Intialising Report File..");
		rowNum = 0; // reset the sheet row number counter
		logger.debug("Row Num set to {}", rowNum);
		logger.debug("Reseting Workbook");
		report = (XSSFWorkbook) workbookUtils.loadWorkbook(); // reset the workbook
		logger.debug("Reseting WorkSheet");
		sheet = workbookUtils.loadWorksheet(reportConstants.reportFileSheetName); //reset the sheet
		insertHeader();
	}
	
	/**
	 * @return Gets the report file path
	 */
	private String getReportFileNameFor(LocalDate date) {
		final String reportFileName = reportConstants.reportFilePath + System.getProperty("file.separator")
				+ reportConstants.reportFileName + "_" + date + ".xlsx";
		logger.debug("Report File Name for date {} is {}",date,reportFileName);
		return reportFileName;
	}
	
	/**
	 * Gets the report file path based on the date passed
	 * @param date for which the report file path is required.
	 * @return file path of report corresponding to given date.<br>
	 * If the date is same as Today, then gives path of report file corresponding to previous day.<br>
	 * Since the report is still not generated for the day
	 */
	public String getReportFileForDate(LocalDate date) {
		logger.debug("Getting Report file path for date {}",date);
		Assert.notNull(date, "Date cannot be null");
		LocalDate reportFileFor = date; 
		if(reportFileFor.equals(LocalDate.now()))
			reportFileFor = reportFileFor.minusDays(1);
		logger.debug("Report file path for {} shall be returned",reportFileFor);
		return getReportFileNameFor(reportFileFor);
	}

	/**
	 * @return the report file currently in creation.
	 */
	public Workbook getReport() {
		return report;
	}
	
}