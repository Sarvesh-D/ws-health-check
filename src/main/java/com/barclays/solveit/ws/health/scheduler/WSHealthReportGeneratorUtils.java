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
import org.springframework.stereotype.Component;

import com.barclays.solveit.ws.health.model.ServiceDetail;
import com.barclays.solveit.ws.health.util.WorkbookUtils;

@Component 
public class WSHealthReportGeneratorUtils {

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
		initReportFile();
	}
	
	void buildReport(List<ServiceDetail> serviceHealthDetails) {
		if(rowNum == 0)
			insertHeader();
		
		for (ServiceDetail serviceDetail : serviceHealthDetails) {
			Row row = sheet.createRow(rowNum++);
			int cellNum = 0;
			row.createCell(cellNum++).setCellValue(LocalTime.now().toString());
			row.createCell(cellNum++).setCellValue(serviceDetail.getEnvironment());
			row.createCell(cellNum++).setCellValue(serviceDetail.getProvider());
			row.createCell(cellNum++).setCellValue(serviceDetail.getDescription());
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

	void saveReport() {
		try(FileOutputStream out = new FileOutputStream(getReportFileName())) {
			workbookUtils.formatAsTable(0,rowNum,0,reportHeaders.length-1);
			workbookUtils.autoAdjustColumnWidth(0, reportHeaders.length-1);
			workbookUtils.createConditionalFormattingRule();
			report.write(out);
			fileCounter++; // to create unique filename for testing under small intervals
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			initReportFile(); // reset report file to start writing data in new file
		}
	}
	
	private void initReportFile() {
		rowNum = 0; // reset the sheet row number counter
		report = (XSSFWorkbook) workbookUtils.loadWorkbook(); // reset the workbook
		sheet = workbookUtils.loadWorksheet("report"); //reset the sheet 
	}

	String getReportFileName() {
		return reportFilePath+System.getProperty("file.separator")+"report_"+LocalDate.now()+".xlsx";
	}

	public Workbook getReport() {
		return report;
	}
	
}