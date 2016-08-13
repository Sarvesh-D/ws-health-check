package com.ds.ws.health.scheduler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.ws.health.model.ServiceDetail;
import com.ds.ws.health.service.WSHealthService;

@Component
class WSHealthReportGenerator {

	@Autowired
	@Qualifier("wSHealthServiceImpl")
	private WSHealthService wsHealthService;

	@Autowired
	private WSHealthReportGeneratorUtils wsHealthReportGeneratorUtils;
	
	@PostConstruct
	private void buildReportOnStartup() {
		buildWSHealthReport();
	}
	
	@PreDestroy
	private void saveReportBeforeShutdown() {
		createReportFile();
	}

	@Scheduled(cron = "${ping.interval}")
	void buildWSHealthReport() {
		List<ServiceDetail> serviceHealthDetails = wsHealthService.getServiceHealthDetails();
		wsHealthReportGeneratorUtils.buildReport(serviceHealthDetails);
	}

	@Scheduled(cron = "${file.rollover.interval}")
	void createReportFile() {
		wsHealthReportGeneratorUtils.saveReport();
	}

}
