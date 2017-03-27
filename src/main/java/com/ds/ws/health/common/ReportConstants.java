package com.ds.ws.health.common;

import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class containing Report constants.<br>
 * This are application critical constants.<br>
 * <b>DO NOT</b> alter these before understanding the significance
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
public class ReportConstants {

    private static final Logger logger = LoggerFactory.getLogger(ReportConstants.class);

    public final String reportFilePath;

    public final String reportFileName;

    public final String reportFileSheetName;

    public final String reportHeaders;

    public final String reportFooter;

    public final String reportFooterLink;

    @Autowired
    private ReportConstants(Properties reportProperties) {
	logger.info("initialising report constants...");
	reportFilePath = reportProperties.getProperty("report.file.path");
	reportFileName = reportProperties.getProperty("report.file.workbook.name");
	reportFileSheetName = reportProperties.getProperty("report.file.sheet.name");
	reportHeaders = reportProperties.getProperty("report.header");
	reportFooter = reportProperties.getProperty("report.footer");
	reportFooterLink = reportProperties.getProperty("report.footer.link");
	logger.info("initialising report constants completed");

	Arrays.stream(ReportConstants.class.getDeclaredFields()).forEach(field -> {
	    try {
		logger.debug("Setting constant [{}] --> [{}]", field.getName(), field.get(this));
	    } catch (IllegalArgumentException | IllegalAccessException e1) {
		e1.printStackTrace();
	    }
	});

    }

}
