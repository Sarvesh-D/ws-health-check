package com.ds.ws.health.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.ws.health.model.EnvironmentView;
import com.ds.ws.health.service.WSHealthService;

@RestController
class WSHealthController {
	
	@Autowired
	@Qualifier("wSHealthServiceImpl")
	private WSHealthService wsHealthService;
	
	@RequestMapping("env/health")
	public EnvironmentView getServiceHealthDetails() {
		EnvironmentView response = new EnvironmentView();
		response.getEnvironments().addAll(wsHealthService.getEnvHealthDetailsFromReport());
		return response;
	}
	
}
