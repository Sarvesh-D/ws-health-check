package com.ds.ws.health.scheduler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.ws.health.model.Service;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.service.WSHealthService;

/**
 * Scheduler class for generating reports
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
class WSHealthReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(WSHealthReportGenerator.class);

    @Autowired
    @Qualifier("wSHealthServiceImpl")
    private WSHealthService wsHealthService;

    @Autowired
    private WSHealthReportGeneratorUtils wsHealthReportGeneratorUtils;

    @Scheduled(cron = "${ping.interval}")
    void buildWSHealthReport() {
	logger.debug("Building report file started on time {}", LocalDateTime.now());
	List<Service> serviceHealthDetails = wsHealthService.getServiceHealthDetails();
	wsHealthReportGeneratorUtils.buildReport(serviceHealthDetails);
    }

    @Scheduled(cron = "${file.rollover.interval}")
    void createReportFile() {
	logger.debug("Saving report file started on time {}", LocalDateTime.now());
	wsHealthReportGeneratorUtils.saveReport();
	/*
	 * After creating report file, the new report file will be empty. To
	 * support com.ds.ws.health.service.WSHealthService.
	 * getEnvHealthDetailsFromReport() service, the report file must contain
	 * some data, hence calling buildWSHealthReport() explicitly instead of
	 * waiting till ping.interval
	 */
	buildWSHealthReport();
    }

    @PostConstruct
    private void buildReportOnStartup() {
	logger.debug("Building report file on startup");
	buildWSHealthReport();
    }

    @PreDestroy
    private void saveReportBeforeShutdown() {
	logger.debug("Saving report file on shutdown");
	createReportFile();
    }

}
