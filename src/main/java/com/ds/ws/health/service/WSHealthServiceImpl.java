package com.ds.ws.health.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.Service.Status;
import com.ds.ws.health.model.ServiceTimeStatus;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.util.WSHealthUtils;
import com.ds.ws.health.util.WorkbookUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation for {@link WSHealthService}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@org.springframework.stereotype.Service("wSHealthServiceImpl")
@Slf4j
public class WSHealthServiceImpl implements WSHealthService {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Autowired
    private WorkbookUtils workbookUtils;

    @Autowired
    private WSHealthReportGeneratorUtils reportUtils;

    @Autowired
    private CoreConstants coreConstants;

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
	log.debug("Rows Fetched = {}", rows.size());

	for (Row row : rows) {
	    Service serviceRow = wsHealthUtils.convertRowToService(row);
	    if (service.equals(serviceRow))
		serviceReport.add(new ServiceTimeStatus(row.getCell(0).toString(), serviceRow.getStatus().toString()));
	}

	return Collections.unmodifiableList(serviceReport);
    }

    @Override
    public List<Service> getServiceHealthDetails() {
	log.info("Getting service health details started...");
	List<Service> serviceDetails = new ArrayList<>(wsHealthUtils.getAllServices());
	Collections.sort(serviceDetails, Service.SERVICE_DETAIL_COMPARATOR);
	for (Service serviceDetail : serviceDetails) {
	    log.debug("Getting service health details for service {} ", serviceDetail);
	    Status serviceStatus = wsHealthUtils.pingURL(serviceDetail.getUri(),
		    coreConstants.connectionTimeoutInMillis) ? Status.UP : Status.DOWN;
	    log.debug("Service is {}", serviceStatus);
	    serviceDetail.setStatus(serviceStatus);
	}
	log.info("Getting service health details completed");
	return Collections.unmodifiableList(serviceDetails);
    }

}