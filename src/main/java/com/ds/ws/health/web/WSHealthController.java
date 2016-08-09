package com.ds.ws.health.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.ServiceDetail;
import com.ds.ws.health.util.WSHealthUtils;

@RestController
public class WSHealthController {
	
	@Autowired
	private WSHealthUtils wSHealthUtils;
	
	@RequestMapping("service/health")
	public List<ServiceDetail> getServiceHealthDetails() {
		return wSHealthUtils.getServiceHealthDetails();
	}
	
	@RequestMapping("env/health")
	public List<Environment> getEnvHealthDetails() {
		return wSHealthUtils.getEnvHealthDetails();
	}

}
