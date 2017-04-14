package com.ds.ws.health.report;

import java.util.Properties;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class WSHealthReportGeneratorUtilsTest extends BaseTest {

    private WSHealthReportGeneratorUtils reportUtils;
    private Properties reportProperties;

    @Before
    public void setUp() {
	reportUtils = WSHealthUtils.instanceOf(WSHealthReportGeneratorUtils.class);
	reportProperties = rootContext.getBean("reportProperties", Properties.class);
    }

    @Test
    // TODO see how to preform this test w/o manually changing date each time.
    public void testGetReportFileForDate() {
	LocalDate date_1 = new LocalDate(2016, 8, 01);
	String actualPath_1 = reportUtils.getReportFileForDate(date_1);
	String expectedPath_1 = reportProperties.getProperty("report.file.path") + System.getProperty("file.separator")
		+ "report_" + "2016-08-01.xlsx";
	// assertTrue(actualPath_1.equalsIgnoreCase(expectedPath_1));

	LocalDate date_2 = new LocalDate(LocalDate.now());
	String actualPath_2 = reportUtils.getReportFileForDate(date_2);
	String expectedPath_2 = reportProperties.getProperty("report.file.path") + System.getProperty("file.separator")
		+ "report_" + "2016-10-26.xlsx";
	// assertTrue(actualPath_2.equalsIgnoreCase(expectedPath_2));
    }

}
