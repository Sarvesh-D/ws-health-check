package com.barclays.solveit.ws.health.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.barclays.solveit.ws.health.model.ServiceDetail;
import com.barclays.solveit.ws.health.util.WSHealthUtils;

@Component
public class WSHealthReportGenerator {
	
	@Autowired
	private WSHealthUtils wSHealthUtils;
	
	@Autowired
	private SchedulerConstants schedulerConstants;
	
	@Autowired
	private Properties reportProperties;
	
	private String reportFilePath;
	
	private String[] reportHeaders;
	
	private int rowNum;
	
	private HSSFWorkbook report;
	
	private HSSFSheet sheet;
	
	private int fileCounter = 1;
	
	@PostConstruct
	private void init() {
		reportFilePath = reportProperties.getProperty("report.file.path");
		reportHeaders = StringUtils.split(reportProperties.getProperty("report.headers"), ",");
		Assert.isTrue(reportHeaders.length == 6, "only 6 report headers are supported");
		report = new HSSFWorkbook();
		sheet = report.createSheet();
	}
	
	@Scheduled(cron = "0 0/1 * * * ?") // ping each minute and build report
	public void buildWSHealthReport() {
		Set<ServiceDetail> serviceHealthDetails = wSHealthUtils.getServiceHealthDetails();
		buildReport(serviceHealthDetails);
	}
	
	@Scheduled(cron = "45 0/5 * * * ?") // save report file every 5 mins
	public void createReportFile() {
		saveReport(report);
		resetReportFile(); // reset report file to start writing data in new file
	}
	
	private void resetReportFile() {
		rowNum = 0; // reset the sheet row number counter
		report = new HSSFWorkbook(); // reset the workbook
		sheet = report.createSheet("report"); //reset the sheet
		fileCounter++; // to create unique filename for testing under small intervals
	}
	
	private void buildReport(Set<ServiceDetail> serviceHealthDetails) {
		if(rowNum == 0)
			insertHeader(sheet);

		for (ServiceDetail serviceDetail : serviceHealthDetails) {
			Row row = sheet.createRow(rowNum++);
			int cellNum = 0;
			row.createCell(cellNum++).setCellValue(LocalDateTime.now().toString());
			row.createCell(cellNum++).setCellValue(serviceDetail.getEnvironment());
			row.createCell(cellNum++).setCellValue(serviceDetail.getProvider());
			row.createCell(cellNum++).setCellValue(serviceDetail.getDescription());
			row.createCell(cellNum++).setCellValue(serviceDetail.getUri());
			row.createCell(cellNum++).setCellValue(serviceDetail.getStatus());
		}
		
	}
	
	private void insertHeader(HSSFSheet sheet) {
		Row row = sheet.createRow(rowNum++);
		int cellNum = 0;
		for (String title : reportHeaders) {
			Cell cell = row.createCell(cellNum++);
			cell.setCellValue(title);
		}
	}

	private void saveReport(HSSFWorkbook serviceHealthReport) {
		try(FileOutputStream out = new FileOutputStream(getReportFileName())) {
			serviceHealthReport.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getReportFileName() {
		return reportFilePath+System.getProperty("file.separator")+"report_"+LocalDate.now()+"_"+fileCounter+".xls";
	}

}
