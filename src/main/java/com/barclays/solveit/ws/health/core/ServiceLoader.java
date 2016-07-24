package com.barclays.solveit.ws.health.core;

import java.util.Set;

import com.barclays.solveit.ws.health.model.ServiceDetail;

public interface ServiceLoader {
	
	Set<ServiceDetail> getServiceDetails();

}
