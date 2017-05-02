package com.ds.ws.health.web;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.ServiceTimeStatus;
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
    public Set<Environment> getEnvHealthDetails() {
	log.info("getEnvHealthDetails Service requested");
	return wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.REAL_TIME);
    }

    @RequestMapping("service/health")
    public List<ServiceTimeStatus> getReportForService(@RequestParam String env, @RequestParam String provider,
	    @RequestParam String uri) {
	log.info("getServiceHealthDetails Service requested");
	Service service = new Service(new Provider(provider, new Environment(env)), uri);
	return wsHealthService.getReportForService(service);
    }

}
