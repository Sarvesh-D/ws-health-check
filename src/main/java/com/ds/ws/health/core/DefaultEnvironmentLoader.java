package com.ds.ws.health.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.util.WSHealthUtils;


/**
 * This class is the heart of ws-health-check.<br>
 * This class loads the {@link Environment}, {@link Provider} and {@link Service}
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@Component
@DependsOn(value="customTrustStoreManager")
public class DefaultEnvironmentLoader implements EnvironmentLoader {

	private static final Logger logger = LoggerFactory.getLogger(DefaultEnvironmentLoader.class); 

	@Autowired
	@Qualifier("environmentProperties")
	private Properties serviceProperties;

	@Autowired
	private CoreConstants coreConstants;

	@Autowired
	private WSHealthUtils wsHealthUtils;

	private final Set<Environment> environments = new HashSet<>();

	@Override
	@Cacheable("environments")
	public final void loadEnvironments() {
		logger.info("Loading environmets started...");
		List<Service> serviceDetails = getServiceDetailsViaProperties();
		for (Service service : serviceDetails) {
			logger.debug("loading Service details for {}", service);
			final String envName = service.getEnvironment();
			final String providerName = service.getProvider();
			final String desc = service.getDescription();
			final String uri = service.getUri();

			final Environment environment = new Environment(envName);
			logger.debug("Environment created {}",environment);
			final Provider provider = new Provider(providerName, envName);
			logger.debug("Provider created {}",provider);
			final Service serviceDetail = new Service(envName, providerName, desc, uri);
			logger.debug("Service created {}",serviceDetail);

			// check if env is already loaded. 
			if(environments.contains(environment)) {
				logger.debug("Environments Set contains ENV {}", envName);
				for (Environment env : environments) {
					if(env.equals(environment)) {
						// if env is already loaded get its components
						Set<Provider> envComponents = env.getComponents();

						logger.debug("Environment {} contains Components {}", envName, envComponents);
						// check if component is loaded in this env.
						if(envComponents.contains(provider)) {
							logger.debug("Env Components contains Component {}", provider);
							for (Provider component : envComponents) {
								if(component.equals(provider)) {
									// if yes then attach the service to it
									component.getServices().add(serviceDetail);
									logger.debug("Service {} added to Component {} in Env {}", serviceDetail,provider,envName);
									break;
								}
							}
						} else {
							// if component is not loaded in this env, then add this component to env components
							logger.debug("Env Components does not contain Component {}", provider);
							provider.getServices().add(serviceDetail);
							env.getComponents().add(provider);
							logger.debug("Service {} added to Component {} in Env {}", serviceDetail,provider,envName);
						}
						break;
					}
				}
			} else { // if not go ahead and load it
				logger.debug("Environments Set does not contain ENV {}", envName);
				provider.getServices().add(serviceDetail);
				environment.getComponents().add(provider);
				logger.debug("Service {} added to Component {} in Env {}", serviceDetail,provider,envName);
				environments.add(environment);
				logger.debug("Environment {} added to Environments Set", envName);
			}

		}
		logger.info("Loading environmets completed.");
	}

	/**
	 * Get the Service details from property file
	 * @return list {@link Service}
	 */
	private List<Service> getServiceDetailsViaProperties() {
		logger.debug("Fetching ENV details via properties...");
		final List<Service> serviceDetails = new ArrayList<>();
		for (Entry<Object,Object> serviceDetailEntry : serviceProperties.entrySet()) {
			String[] serviceDetail = StringUtils.split(StringUtils.trimToEmpty(serviceDetailEntry.getValue().toString()),
					coreConstants.serviceDetailsSeparatorKey);

			logger.debug("service Detail {}",Arrays.toString(serviceDetail));
			Assert.isTrue(serviceDetail.length == 4,
					"service details currently supports only 4 properties viz [enviornment,provider,description,uri]");

			final String envName = serviceDetail[0];
			final String provider = serviceDetail[1];
			final String desc = serviceDetail[2];
			final String uri = serviceDetail[3];


			Service service = new Service(envName, provider,desc,
					wsHealthUtils.cleanUrl(uri));
			serviceDetails.add(service);

			logger.debug("Service details fetched {}",service);
			
		}
		logger.debug("Fetching Service details via properties completed");
		return Collections.unmodifiableList(serviceDetails);
	}

	private DefaultEnvironmentLoader() {}

	@Override
	public Set<Environment> getEnvironments() {
		return Collections.unmodifiableSet(this.environments);
	}

}
