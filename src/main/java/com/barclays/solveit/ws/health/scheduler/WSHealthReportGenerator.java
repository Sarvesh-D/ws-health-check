package com.barclays.solveit.ws.health.scheduler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.barclays.solveit.ws.health.model.ServiceDetail;
import com.barclays.solveit.ws.health.util.WSHealthUtils;
import com.barclays.solveit.ws.health.util.WorkbookUtils;

@Component
public class WSHealthReportGenerator {

	@Autowired
	private WSHealthUtils wSHealthUtils;

	@Autowired
	private WSHealthReportGeneratorUtils wsHealthReportGeneratorUtils;

	@Autowired
	private SchedulerConstants schedulerConstants;

	@Scheduled(cron = "0 0/1 * * * ?") // ping each minute and build report
	public void buildWSHealthReport() {
		List<ServiceDetail> serviceHealthDetails = wSHealthUtils.getServiceHealthDetails();
		wsHealthReportGeneratorUtils.buildReport(serviceHealthDetails);
	}

	@Scheduled(cron = "45 0/2 * * * ?") // save report file every 5 mins
	public void createReportFile() {
		wsHealthReportGeneratorUtils.saveReport();
		wsHealthReportGeneratorUtils.initReportFile(); // reset report file to start writing data in new file
	}

	@Component
	private static class WSHealthReportGeneratorUtils {

		@Autowired
		private WorkbookUtils workbookUtils;

		@Autowired
		private Properties reportProperties;

		private String reportFilePath;

		private String[] reportHeaders;

		private int rowNum = 0;

		private Workbook report;

		private Sheet sheet;

		private int fileCounter = 1;

		@PostConstruct
		private void init() {
			reportFilePath = reportProperties.getProperty("report.file.path");
			reportHeaders = StringUtils.split(reportProperties.getProperty("report.headers"), ",");
			Assert.isTrue(reportHeaders.length == 6, "only 6 report headers are supported");
			initReportFile();
		}

		private void buildReport(List<ServiceDetail> serviceHealthDetails) {
			if(rowNum == 0)
				insertHeader();
			
			for (ServiceDetail serviceDetail : serviceHealthDetails) {
				Row row = sheet.createRow(rowNum++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(LocalTime.now().toString());
				row.createCell(cellNum++).setCellValue(serviceDetail.getEnvironment());
				row.createCell(cellNum++).setCellValue(serviceDetail.getProvider());
				row.createCell(cellNum++).setCellValue(serviceDetail.getDescription());
				//row.createCell(cellNum++).setCellValue(serviceDetail.getUri());
				workbookUtils.createHyperlinkCell(row.createCell(cellNum++), serviceDetail.getUri());
				row.createCell(cellNum).setCellValue(serviceDetail.getStatus());
			}

		}

		private void insertHeader() {
			Row row = sheet.createRow(rowNum++);
			int cellNum = 0;
			for (String title : reportHeaders) {
				row.createCell(cellNum++).setCellValue(title);
			}
		}

		private void saveReport() {
			try(FileOutputStream out = new FileOutputStream(getReportFileName())) {
				workbookUtils.formatAsTable(rowNum,reportHeaders.length);
				workbookUtils.autoAdjustColumnWidth(0, reportHeaders.length-1);
				report.write(out);
				fileCounter++; // to create unique filename for testing under small intervals
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void initReportFile() {
			rowNum = 0; // reset the sheet row number counter
			workbookUtils.loadWorkbook(); // reset the workbook
			report = (XSSFWorkbook) workbookUtils.getWorkbook(); 
			sheet = report.createSheet("report"); //reset the sheet
		}

		private String getReportFileName() {
			return reportFilePath+System.getProperty("file.separator")+"report_"+LocalDate.now()+"_"+fileCounter+".xlsx";
		}

	}

}
