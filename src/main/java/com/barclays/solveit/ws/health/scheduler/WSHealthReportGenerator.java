package com.barclays.solveit.ws.health.scheduler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.barclays.solveit.ws.health.model.ServiceDetail;
import com.barclays.solveit.ws.health.util.WSHealthUtils;

@Component
class WSHealthReportGenerator {

	@Autowired
	private WSHealthUtils wsHealthUtils;

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
		List<ServiceDetail> serviceHealthDetails = wsHealthUtils.getServiceHealthDetails();
		wsHealthReportGeneratorUtils.buildReport(serviceHealthDetails);
	}

	@Scheduled(cron = "${file.rollover.interval}")
	void createReportFile() {
		wsHealthReportGeneratorUtils.saveReport();
	}

}
