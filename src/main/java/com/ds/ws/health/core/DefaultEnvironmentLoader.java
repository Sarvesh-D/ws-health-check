package com.ds.ws.health.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * This class loads the {@link Environment}, {@link Provider} and
 * {@link Service}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
@DependsOn(value = "customTrustStoreManager")
public class DefaultEnvironmentLoader implements EnvironmentLoader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEnvironmentLoader.class);

    @Autowired
    @Qualifier("environmentProperties")
    private Properties serviceProperties;

    @Autowired
    private CoreConstants coreConstants;

    @Autowired
    private WSHealthUtils wsHealthUtils;

    private Set<Environment> environments = new HashSet<>();

    private DefaultEnvironmentLoader() {
    }

    @Override
    public Set<Environment> getEnvironments() {
	return Collections.unmodifiableSet(this.environments);
    }

    @Override
    public final void loadEnvironments() {
	logger.info("Loading environmets started...");
	List<Service> serviceDetails = getServiceDetailsViaProperties();

	serviceDetails.stream().map(service -> buildEnvHierarchyFromService(service)).forEach(environments::add);

	logger.info("Loading environmets completed.");
    }

    private Environment buildEnvHierarchyFromService(Service service) {
	logger.debug("Building Env Hierarchy from Service {}", service);
	final String envName = service.getEnvironment();
	final String providerName = service.getProvider();
	Provider providerToCheck = new Provider(providerName, envName);

	final Optional<Environment> environment = environments.stream()
		.filter(env -> env.equals(new Environment(envName))).findFirst();

	if (environment.isPresent()) {
	    Set<Provider> envComponents = environment.get().getComponents();
	    Optional<Provider> provider = envComponents.stream().filter(comp -> comp.equals(providerToCheck))
		    .findFirst();
	    // check if component is loaded in this env.
	    if (provider.isPresent()) {
		logger.debug("Env Components contains Component {}", provider);
		provider.get().getServices().add(service);
	    } else {
		// if component is not loaded in this env, then add this
		// component to env components
		logger.debug("Env Components does not contain Component {}", providerName);
		providerToCheck.getServices().add(service);
		environment.get().getComponents().add(providerToCheck);
	    }
	    logger.debug("Service {} added to Component {} in Env {}", service, providerName, envName);
	    return environment.get();
	} else {
	    logger.debug("Environments Set does not contain ENV {}", environment);
	    providerToCheck.getServices().add(service);
	    Environment newEnvironment = new Environment(envName);
	    newEnvironment.getComponents().add(providerToCheck);
	    logger.debug("Service {} added to Component {} in Env {}", service, providerName, envName);
	    return newEnvironment;
	}
    }

    /**
     * Get the Service details from property file
     * 
     * @return list {@link Service}
     */
    private List<Service> getServiceDetailsViaProperties() {
	logger.debug("Fetching ENV details via properties...");
	final List<Service> serviceDetails = serviceProperties.values().stream()
		.map(serviceDetailEntry -> mapToService(serviceDetailEntry)).collect(Collectors.toList());
	logger.debug("Fetching Service details via properties completed");
	return Collections.unmodifiableList(serviceDetails);
    }

    private Service mapToService(Object serviceDetailEntry) {
	String[] serviceDetail = StringUtils.split(StringUtils.trimToEmpty(serviceDetailEntry.toString()),
		coreConstants.serviceDetailsSeparatorKey);

	logger.debug("service Detail {}", Arrays.toString(serviceDetail));
	Assert.isTrue(serviceDetail.length == 4,
		"service details currently supports only 4 properties viz [enviornment,provider,description,uri]");

	final String envName = serviceDetail[0];
	final String provider = serviceDetail[1];
	final String desc = serviceDetail[2];
	final String uri = serviceDetail[3];

	Service service = new Service(envName, provider, desc, wsHealthUtils.cleanUrl(uri));
	logger.debug("Service created {}", service);
	return service;
    }

}
