package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Service.Status;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.ServiceTimeStatus;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;
import com.ds.ws.health.util.WorkbookUtils;

/**
 * Default implementation for {@link WSHealthService} 
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 *
 */
@org.springframework.stereotype.Service("wSHealthServiceImpl")
public class WSHealthServiceImpl implements WSHealthService {

	private static final Logger logger = LoggerFactory.getLogger(WSHealthServiceImpl.class);

	@Autowired
	private WSHealthUtils wsHealthUtils;
	
	@Autowired
	private WorkbookUtils workbookUtils;
	
	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;

	@Autowired
	private CoreConstants coreConstants;

	@Override
	public List<Service> getServiceHealthDetails() {
		logger.info("Getting service health details started...");
		List<Service> serviceDetails = new ArrayList<>(wsHealthUtils.getAllServices());
		Collections.sort(serviceDetails, Service.SERVICE_DETAIL_COMPARATOR);
		for (Service serviceDetail : serviceDetails) {
			logger.debug("Getting service health details for service {} ",serviceDetail);
			Status serviceStatus = wsHealthUtils.pingURL(serviceDetail.getUri(), coreConstants.connectionTimeoutInMillis)
					? Status.UP : Status.DOWN;
			logger.debug("Service is {}",serviceStatus);
			serviceDetail.setStatus(serviceStatus);
		}
		logger.info("Getting service health details completed");
		return Collections.unmodifiableList(serviceDetails);
	}
	
	@Override
	public List<Environment> getEnvHealthDetails(EnvDetailsFetchMode fetchMode) {
		return WSHealthUtils.instanceOf(fetchMode.getStrategy()).getEnvHealthDetails();
	}
	
	@Override
	public List<ServiceTimeStatus> getReportForService(Service service) {
		List<ServiceTimeStatus> serviceReport = new ArrayList<>();
		// Load workbook and sheet
		Workbook workbook = workbookUtils.loadWorkbook(reportUtils.getReport());
		Sheet reportSheet = workbookUtils.loadWorksheet(workbook.getSheet("report"));

		// excluding header and footer
		List<Row> rows = workbookUtils.getRowData(reportSheet.getFirstRowNum() + 1, reportSheet.getLastRowNum() - 1);
		logger.debug("Rows Fetched = {}",rows.size());
		
		for (Row row : rows) {
			Service serviceRow = wsHealthUtils.convertRowToService(row);
			if(service.equals(serviceRow))
				serviceReport.add(new ServiceTimeStatus(row.getCell(0).toString(), serviceRow.getStatus().toString()));
		}
		
		return Collections.unmodifiableList(serviceReport);
	}

}	