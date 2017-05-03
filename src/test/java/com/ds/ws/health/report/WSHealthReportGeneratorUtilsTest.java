package com.ds.ws.health.report;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.common.ReportConstants;
import com.ds.ws.health.service.MonitoringService;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class WSHealthReportGeneratorUtilsTest extends BaseTest {

    private WSHealthReportGeneratorUtils reportUtils;
    private ReportConstants reportConstants;
    private MonitoringService healthService;

    @Before
    public void setUp() {
	reportUtils = WSHealthUtils.instanceOf(WSHealthReportGeneratorUtils.class);
	reportConstants = WSHealthUtils.instanceOf(ReportConstants.class);
	healthService = WSHealthUtils.instanceOf(MonitoringService.class);
    }

    @Test
    public void test() {
	reportUtils.buildReport(healthService.getServiceHealthDetails());
	reportUtils.saveReport();
	String reportFilePath = reportUtils.getReportFileForDate(LocalDate.now());
	File reportFile = new File(reportFilePath);
	assertTrue("Path to report File is not valid", new File(reportConstants.reportFilePath).exists());
	assertTrue("Report File not saved to desired path", reportFile.exists());
	assertTrue("Report File saved is not readable", reportFile.canRead());
    }

}
