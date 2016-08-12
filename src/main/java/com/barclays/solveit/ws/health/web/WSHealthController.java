package com.barclays.solveit.ws.health.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barclays.solveit.ws.health.model.Environment;
import com.barclays.solveit.ws.health.util.WSHealthUtils;

@RestController
final class WSHealthController {
	
	@Autowired
	private WSHealthUtils wsHealthUtils;
	
	@RequestMapping("env/health")
	private List<Environment> getServiceHealthDetails() {
		return wsHealthUtils.getEnvHealthDetailsFromReport();
	}
	
}
