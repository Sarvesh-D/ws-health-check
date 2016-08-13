package com.ds.ws.health.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.service.WSHealthService;

@RestController
final class WSHealthController {
	
	@Autowired
	@Qualifier("wSHealthServiceImpl")
	private WSHealthService wsHealthService;
	
	@RequestMapping("env/health")
	private List<Environment> getServiceHealthDetails() {
		return wsHealthService.getEnvHealthDetailsFromReport();
	}
	
}
