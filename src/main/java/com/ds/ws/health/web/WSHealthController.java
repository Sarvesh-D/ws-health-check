package com.barclays.solveit.ws.health.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barclays.solveit.ws.health.model.EnvironmentDetailsViewResponse;
import com.barclays.solveit.ws.health.model.Service;
import com.barclays.solveit.ws.health.model.ServiceTimeStatusResponse;
import com.barclays.solveit.ws.health.service.EnvDetailsFetchMode;
import com.barclays.solveit.ws.health.service.WSHealthService;

/**
 * Controller for getting {@link EnvironmentDetailsViewResponse}
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 *
 */
@RestController
class WSHealthController {
	
	private static final Logger logger = LoggerFactory.getLogger(WSHealthController.class);
	
	@Autowired
	@Qualifier("wSHealthServiceImpl")
	private WSHealthService wsHealthService;
	
	@RequestMapping("env/health")
	public EnvironmentDetailsViewResponse getEnvHealthDetails() {
		logger.info("getEnvHealthDetails Service requested");
		EnvironmentDetailsViewResponse response = new EnvironmentDetailsViewResponse();
		response.getEnvironments().addAll(wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.NEAR_REAL_TIME));
		return response;
	}
	
	@RequestMapping("service/health")
	public ServiceTimeStatusResponse getReportForService(@RequestParam String env, @RequestParam String provider,
			@RequestParam String ver, @RequestParam String uri) {
		logger.info("getServiceHealthDetails Service requested");
		Service service = new Service(env, provider, "NA", uri);
		ServiceTimeStatusResponse response = new ServiceTimeStatusResponse();
		response.getServiceTimes().addAll(wsHealthService.getReportForService(service));
		return response;
	}
	
}
