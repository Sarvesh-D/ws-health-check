package com.ds.ws.health.report;

import java.util.Properties;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/root-context.xml" })
public class WSHealthReportGeneratorUtilsTest {

    @Autowired
    private WSHealthReportGeneratorUtils reportUtils;

    @Autowired
    private Properties reportProperties;

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
