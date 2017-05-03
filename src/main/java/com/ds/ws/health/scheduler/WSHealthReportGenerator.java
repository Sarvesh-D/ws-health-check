package com.ds.ws.health.scheduler;

import java.util.Set;

import javax.annotation.PreDestroy;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.ws.health.model.Service;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.service.MonitoringService;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler class for generating reports
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
@Slf4j
class WSHealthReportGenerator {

    @Autowired
    @Qualifier("wSHealthServiceImpl")
    private MonitoringService wsHealthService;

    @Autowired
    private WSHealthReportGeneratorUtils wsHealthReportGeneratorUtils;

    @Scheduled(cron = "${ping.interval}")
    void buildWSHealthReport() {
	log.debug("Building report file Started on time {}", LocalDateTime.now());
	Set<Service> serviceHealthDetails = wsHealthService.getServiceHealthDetails();
	wsHealthReportGeneratorUtils.buildReport(serviceHealthDetails);
	log.debug("Building report file Completed on time {}", LocalDateTime.now());
    }

    @Scheduled(cron = "${file.rollover.interval}")
    void createReportFile() {
	log.debug("Saving report file started on time {}", LocalDateTime.now());
	wsHealthReportGeneratorUtils.saveReport();
	log.debug("Saving report file completed on time {}", LocalDateTime.now());
	buildWSHealthReport();
    }

    @PreDestroy
    private void saveReportBeforeShutdown() {
	log.debug("Saving report file on shutdown");
	createReportFile();
    }

}
