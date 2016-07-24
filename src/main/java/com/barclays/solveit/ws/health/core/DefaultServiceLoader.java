package com.barclays.solveit.ws.health.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.barclays.solveit.ws.health.common.CoreConstants;
import com.barclays.solveit.ws.health.model.ServiceDetail;

@Component
public class DefaultServiceLoader implements ServiceLoader {

	@Autowired
	private Properties serviceProperties;

	@Autowired
	private CoreConstants coreConstants;

	private final Set<ServiceDetail> serviceDetails = new HashSet<>();

	@PostConstruct
	private final void loadServices() {
		final String services = serviceProperties.getProperty(coreConstants.servicesKey);
		Assert.notNull(services, "no property with key ["+coreConstants.servicesKey+"] found in service.properties");
		if(StringUtils.isNotBlank(services)) {
			String[] serviceArr = StringUtils.split(services, coreConstants.servicesSeparatorKey);
			for (String service : serviceArr) {
				String[] serviceDetailsArr = StringUtils.split(StringUtils.trimToEmpty(service),
						coreConstants.serviceDetailsSeparatorKey);
				Assert.isTrue(serviceDetailsArr.length == 4,
						"service details currently supports only 4 properties viz [enviornment,provider,description,uri]");
				serviceDetails.add(new ServiceDetail(serviceDetailsArr[0], serviceDetailsArr[1], serviceDetailsArr[2],
						serviceDetailsArr[3]));
			}
		}
	}

	private DefaultServiceLoader() {}

	@Override
	public Set<ServiceDetail> getServiceDetails() {
		return Collections.unmodifiableSet(this.serviceDetails);
	}

}
