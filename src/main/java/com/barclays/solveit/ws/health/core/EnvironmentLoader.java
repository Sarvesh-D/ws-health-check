package com.barclays.solveit.ws.health.core;

import java.util.Map;
import java.util.Set;

import com.barclays.solveit.ws.health.model.ComponentKey;
import com.barclays.solveit.ws.health.model.ServiceDetail;

public interface EnvironmentLoader {
	
	Map<String, Map<ComponentKey, Set<ServiceDetail>>> getEnvironmentDetails();

}
