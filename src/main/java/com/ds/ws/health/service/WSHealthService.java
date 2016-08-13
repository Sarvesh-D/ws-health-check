package com.ds.ws.health.service;

import java.util.List;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.ServiceDetail;

public interface WSHealthService {

	List<ServiceDetail> getServiceHealthDetails();

	List<Environment> getEnvHealthDetailsFromReport();

	List<Environment> getEnvHealthDetails();

}