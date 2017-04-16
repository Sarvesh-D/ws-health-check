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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.ServiceTimeStatusResponse;
import com.ds.ws.health.util.WSHealthUtils;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class DefaultEnvironmentLoader implements EnvironmentLoader {

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
	log.info("Loading environmets started...");
	List<Service> serviceDetails = getServiceDetailsViaProperties();
	serviceDetails.stream().map(service -> buildEnvHierarchyFromService(service)).forEach(environments::add);
	log.info("Loading environmets completed.");
    }

    private Environment buildEnvHierarchyFromService(Service service) {
	log.debug("Building Env Hierarchy from Service {}", service);
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
		log.debug("Env Components contains Component {}", provider);
		provider.get().getServices().add(service);
	    } else {
		// if component is not loaded in this env, then add this
		// component to env components
		log.debug("Env Components does not contain Component {}", providerName);
		providerToCheck.getServices().add(service);
		environment.get().getComponents().add(providerToCheck);
	    }
	    log.debug("Service {} added to Component {} in Env {}", service, providerName, envName);
	    return environment.get();
	} else {
	    log.debug("Environments Set does not contain ENV {}", environment);
	    providerToCheck.getServices().add(service);
	    Environment newEnvironment = new Environment(envName);
	    newEnvironment.getComponents().add(providerToCheck);
	    log.debug("Service {} added to Component {} in Env {}", service, providerName, envName);
	    return newEnvironment;
	}
    }

    /**
     * Get the Service details from property file
     * 
     * @return list {@link Service}
     */
    private List<Service> getServiceDetailsViaProperties() {
	log.debug("Fetching ENV details via properties...");
	final List<Service> serviceDetails = serviceProperties.values().stream()
		.map(serviceDetailEntry -> mapToService(serviceDetailEntry)).collect(Collectors.toList());
	log.debug("Fetching Service details via properties completed");
	return Collections.unmodifiableList(serviceDetails);
    }

    private Service mapToService(Object serviceDetailEntry) {
	String[] serviceDetail = StringUtils.split(StringUtils.trimToEmpty(serviceDetailEntry.toString()),
		coreConstants.serviceDetailsSeparatorKey);

	log.debug("service Detail {}", Arrays.toString(serviceDetail));
	Assert.isTrue(serviceDetail.length == 4,
		"service details currently supports only 4 properties viz [enviornment,provider,description,uri]");

	final String envName = serviceDetail[0];
	final String provider = serviceDetail[1];
	final String desc = serviceDetail[2];
	final String uri = serviceDetail[3];

	Service service = new Service(envName, provider, wsHealthUtils.cleanUrl(uri), desc,
		new ServiceTimeStatusResponse());
	log.debug("Service created {}", service);
	return service;
    }

}
