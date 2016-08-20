package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.model.Component;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.ServiceDetail;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;
import com.ds.ws.health.util.WorkbookUtils;

@Service("wSHealthServiceImpl")
public class WSHealthServiceImpl implements WSHealthService {
	
	@Autowired
	private WSHealthUtils wsHealthUtils;
	
	@Autowired
	private CoreConstants coreConstants;
	
	@Autowired
	private WorkbookUtils workbookUtils;
	
	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;

	@Override
	public List<ServiceDetail> getServiceHealthDetails() {
		List<ServiceDetail> serviceDetails = new ArrayList<>(wsHealthUtils.getAllServices());
		Collections.sort(serviceDetails, ServiceDetail.SERVICE_DETAIL_COMPARATOR);
		for (ServiceDetail serviceDetail : serviceDetails) {
			String serviceStatus = wsHealthUtils.pingURL(serviceDetail.getUri(), coreConstants.connectionTimeoutInMillis)
					? coreConstants.serviceStatusPassed : coreConstants.serviceStatusFailed;
			serviceDetail.setStatus(serviceStatus);
		}
		return Collections.unmodifiableList(serviceDetails);
	}
	
	@Override
	public List<Environment> getEnvHealthDetailsFromReport() {
		List<Environment> environmentDetails = new ArrayList<>(wsHealthUtils.getAllEnvironments());
		Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
		
		// Load workbook and sheet
		Workbook workbook = workbookUtils.loadWorkbook(reportUtils.getReport());
		Sheet reportSheet = workbookUtils.loadWorksheet(workbook.getSheet("report"));
		
		final int rowEndIndex = reportSheet.getLastRowNum();
		final int rowStartIndex = rowEndIndex - (wsHealthUtils.getAllServices().size()-1); // index based
		
		List<Row> rows = workbookUtils.getRowData(rowStartIndex, rowEndIndex);
		Set<ServiceDetail> serviceDetailsFromReport = new HashSet<>();
		
		for (Row row : rows) {
			ServiceDetail serviceDetail = new ServiceDetail(row.getCell(1).getStringCellValue(),
					row.getCell(2).getStringCellValue(), row.getCell(3).getStringCellValue(),
					row.getCell(4).getStringCellValue());
			serviceDetail.setStatus(row.getCell(5).getStringCellValue());
			serviceDetailsFromReport.add(serviceDetail);
		}
		
		for (Environment environmentDetail : environmentDetails) {
			for (Component component : environmentDetail.getComponents()) {
				for (ServiceDetail serviceDetail : component.getServices()) {
					for (ServiceDetail serviceDetailFromReport : serviceDetailsFromReport) {
						if(serviceDetail.equals(serviceDetailFromReport))
							serviceDetail.setStatus(serviceDetailFromReport.getStatus());
					}
				}
			}
		}
		
		return Collections.unmodifiableList(environmentDetails);
	}
	
	@Override
	public List<Environment> getEnvHealthDetails() {
		List<Environment> environmentDetails = new ArrayList<>(wsHealthUtils.getAllEnvironments());
		Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
		for (Environment environmentDetail : environmentDetails) {
			for (Component component : environmentDetail.getComponents()) {
				for (ServiceDetail serviceDetail : component.getServices()) {
					String status = wsHealthUtils.pingURL(serviceDetail.getUri(), coreConstants.connectionTimeoutInMillis)
							? coreConstants.serviceStatusPassed : coreConstants.serviceStatusFailed;
					serviceDetail.setStatus(status);
				}
			}
		}
		return Collections.unmodifiableList(environmentDetails);
	}

}
