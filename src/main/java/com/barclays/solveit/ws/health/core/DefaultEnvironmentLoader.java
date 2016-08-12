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
import com.barclays.solveit.ws.health.model.ComponentKey;
import com.barclays.solveit.ws.health.model.ServiceDetail;

@Service
final class DefaultEnvironmentLoader implements EnvironmentLoader {

	@Autowired
	private Properties environmentProperties;

	@Autowired
	private CoreConstants coreConstants;

	private final Map<String,Map<ComponentKey,Set<ServiceDetail>>> environments = new HashMap<>();

	@PostConstruct
	private final void loadEnvironments() {
		final String services = environmentProperties.getProperty(coreConstants.environmentsKey);
		Assert.notNull(services, "no property with key ["+coreConstants.environmentsKey+"] found in service.properties");
		if(StringUtils.isNotBlank(services)) {
			String[] serviceArr = StringUtils.split(services, coreConstants.environmentsSeparatorKey);
			for (String service : serviceArr) {
				String[] serviceDetailsArr = StringUtils.split(StringUtils.trimToEmpty(service),
						coreConstants.environmentDetailsSeparatorKey);

				Assert.isTrue(serviceDetailsArr.length == 5,
						"environment details currently supports only 5 properties viz [enviornment,provider,provider_version,description,uri]");

				final String env = serviceDetailsArr[0];
				final String provider = serviceDetailsArr[1];
				final String providerVersion = serviceDetailsArr[2];
				final String desc = serviceDetailsArr[3];
				final String uri = serviceDetailsArr[4];
				
				final ComponentKey key = new ComponentKey(provider, providerVersion, env);

				final ServiceDetail serviceDetail = new ServiceDetail(env, provider, desc, uri);

				if(environments.containsKey(env)) {
					Map<ComponentKey,Set<ServiceDetail>> envComponents = environments.get(env);
					if(envComponents.containsKey(key)) {
						envComponents.get(key).add(serviceDetail);
					} else {
						Set<ServiceDetail> serviceDetails = new HashSet<>();
						serviceDetails.add(serviceDetail);
						envComponents.put(key, serviceDetails);
					}
				} else {
					Map<ComponentKey,Set<ServiceDetail>> envComponents = new HashMap<>();
					Set<ServiceDetail> serviceDetails = new HashSet<>();
					serviceDetails.add(serviceDetail);
					envComponents.put(key, serviceDetails);
					environments.put(env, envComponents);
				}

			}
		}
	}

	private DefaultEnvironmentLoader() {}

	@Override
	public Map<String, Map<ComponentKey, Set<ServiceDetail>>> getEnvironmentDetails() {
		return Collections.unmodifiableMap(this.environments);
	}

}
