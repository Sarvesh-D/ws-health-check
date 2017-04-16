package com.ds.ws.health.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ds.ws.health.model.EnvironmentDetailsViewResponse;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.ServiceTimeStatusResponse;
import com.ds.ws.health.service.EnvDetailsFetchMode;
import com.ds.ws.health.service.WSHealthService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for getting {@link EnvironmentDetailsViewResponse}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@RestController
@Slf4j
class WSHealthController {

    @Autowired
    @Qualifier("wSHealthServiceImpl")
    private WSHealthService wsHealthService;

    @RequestMapping("env/health")
    public EnvironmentDetailsViewResponse getEnvHealthDetails() {
	log.info("getEnvHealthDetails Service requested");
	EnvironmentDetailsViewResponse response = new EnvironmentDetailsViewResponse();
	response.getEnvironments().addAll(wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.REAL_TIME));
	return response;
    }

    @RequestMapping("service/health")
    public ServiceTimeStatusResponse getReportForService(@RequestParam String env, @RequestParam String provider,
	    @RequestParam String uri) {
	log.info("getServiceHealthDetails Service requested");
	Service service = new Service(env, provider, uri);
	ServiceTimeStatusResponse response = new ServiceTimeStatusResponse();
	response.getServiceTimes().addAll(wsHealthService.getReportForService(service));
	return response;
    }

}
