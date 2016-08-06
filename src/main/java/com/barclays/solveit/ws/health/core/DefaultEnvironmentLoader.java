package com.barclays.solveit.ws.health.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.barclays.solveit.ws.health.common.CoreConstants;
import com.barclays.solveit.ws.health.model.ServiceDetail;

@Service
public class DefaultEnvironmentLoader implements EnvironmentLoader {

	@Autowired
	private Properties serviceProperties;

	@Autowired
	private CoreConstants coreConstants;

	private final Map<String,Map<String,Set<ServiceDetail>>> environments = new HashMap<>();

	@PostConstruct
	private final void loadEnvironments() {
		final String services = serviceProperties.getProperty(coreConstants.servicesKey);
		Assert.notNull(services, "no property with key ["+coreConstants.servicesKey+"] found in service.properties");
		if(StringUtils.isNotBlank(services)) {
			String[] serviceArr = StringUtils.split(services, coreConstants.servicesSeparatorKey);
			for (String service : serviceArr) {
				String[] serviceDetailsArr = StringUtils.split(StringUtils.trimToEmpty(service),
						coreConstants.serviceDetailsSeparatorKey);

				Assert.isTrue(serviceDetailsArr.length == 4,
						"service details currently supports only 4 properties viz [enviornment,provider,description,uri]");

				final String env = serviceDetailsArr[0];
				final String provider = serviceDetailsArr[1];
				final String desc = serviceDetailsArr[2];
				final String uri = serviceDetailsArr[3];

				final ServiceDetail serviceDetail = new ServiceDetail(env, provider, desc, uri);

				if(environments.containsKey(env)) {
					Map<String,Set<ServiceDetail>> envComponents = environments.get(env);
					if(envComponents.containsKey(provider)) {
						envComponents.get(provider).add(serviceDetail);
					} else {
						Set<ServiceDetail> serviceDetails = new HashSet<>();
						serviceDetails.add(serviceDetail);
						environments.get(env).put(provider, serviceDetails);
					}
				} else {
					Map<String,Set<ServiceDetail>> envComponents = new HashMap<>();
					Set<ServiceDetail> serviceDetails = new HashSet<>();
					serviceDetails.add(serviceDetail);
					envComponents.put(provider, serviceDetails);
					environments.put(env, envComponents);
				}

			}
		}
	}

	private DefaultEnvironmentLoader() {}

	@Override
	public Map<String,Map<String,Set<ServiceDetail>>> getEnvironmentDetails() {
		return Collections.unmodifiableMap(this.environments);
	}



}
