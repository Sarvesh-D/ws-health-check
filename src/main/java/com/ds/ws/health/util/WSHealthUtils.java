package com.ds.ws.health.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.core.EnvironmentLoader;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.Service.ServiceStatus;
import com.ds.ws.health.model.ServiceTimeStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for {@link Environment} , {@link Provider} , {@link Service}
 * related info
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@org.springframework.stereotype.Service
@Slf4j
public class WSHealthUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Autowired
    @Qualifier("defaultEnvironmentLoader")
    private EnvironmentLoader environmentLoader;

    @Autowired
    private CoreConstants coreConstants;

    /**
     * Factory method to get an object of type T from the spring context
     * 
     * @param type
     *            Class of object
     * @return initialised object from spring context
     */
    public static <T> T instanceOf(Class<T> type) {
	return type.cast(context.getBean(type));
    }

    /**
     * Cleans the given URL string by removing request parameters from the
     * URL.<br>
     * This also removes the <b>?</b> preceding the request params
     * 
     * @param url
     *            to be cleaned
     * @return cleaned URL string after stripping the request params
     */
    public String cleanUrl(String url) {
	Assert.notNull(url, "URL cannot be null");
	if (url.contains("?"))
	    log.debug("URL {} may contain request parameters. Stripping request params.", url);
	return StringUtils.substringBefore(url, "?");
    }

    /**
     * Converts the rows to a {@link Service} object
     * 
     * @param rows
     *            list of rows to convert to Service. Should not be null
     * @return List of {@link Service}
     */
    public List<Service> convertRowsToService(List<Row> rows) {
	List<Service> serviceDetailsFromReport = new ArrayList<>();
	for (Row row : rows) {
	    if (null == row)
		continue;
	    serviceDetailsFromReport.add(convertRowToService(row));
	}
	return serviceDetailsFromReport;
    }

    /**
     * Converts the given row to a {@link Service} object
     * 
     * @param row
     *            to convert to Service. Should not be null
     * @return {@link Service}
     */
    public Service convertRowToService(Row row) {
	Assert.notNull(row, "Row cannot be null");
	Service service = new Service(row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue(),
		row.getCell(4).getStringCellValue(), row.getCell(3).getStringCellValue());
	service.setStatus(ServiceStatus.valueOf(row.getCell(5).toString()));
	return getLoadedService(service);
    }

    /**
     * Counts the number of times a particular object occurs in the collection
     * 
     * @param inputCollection
     *            containing 0 or more occurrence of match object
     * @param match
     *            Object to be checked in collection
     * @return count of times the match appears in the collection
     */
    public <T> int countMatches(Collection<T> inputCollection, final T match) {
	return CollectionUtils.countMatches(inputCollection, new Predicate() {
	    @Override
	    public boolean evaluate(Object object) {
		return match.getClass().cast(object).equals(match);
	    }
	});
    }

    /**
     * @return Set of all {@link Provider} across all {@link Environment}
     */
    @Cacheable("environments")
    public Set<Provider> getAllComponents() {
	Set<Provider> components = new HashSet<>();
	for (Environment environment : getAllEnvironments()) {
	    components.addAll(environment.getComponents());
	}
	return Collections.unmodifiableSet(components);
    }

    /**
     * @return Set of all {@link Environment}
     */
    @Cacheable("environments")
    public Set<Environment> getAllEnvironments() {
	return Collections.unmodifiableSet(environmentLoader.getEnvironments());
    }

    /**
     * @return Set of all {@link Service} across all {@link Provider} across all
     *         {@link Environment}
     */
    @Cacheable("environments")
    public Set<Service> getAllServices() {
	Set<Service> services = new HashSet<>();

	for (Environment environment : getAllEnvironments()) {
	    for (Provider component : environment.getComponents()) {
		services.addAll(component.getServices());
	    }
	}

	return Collections.unmodifiableSet(services);
    }

    /**
     * Gets Providers of given environment
     * 
     * @param envName
     * @return Set of Providers
     */
    @Cacheable("environments")
    public Set<Provider> getComponentsByEnv(String envName) {
	Assert.isTrue(getAllEnvironments().contains(new Environment(envName)),
		"No environment with name [" + envName + "] mapped.");

	for (Environment environment : getAllEnvironments()) {
	    if (environment.getName().equals(envName))
		return environment.getComponents();
	}

	return null;
    }

    /**
     * Gets the loaded Provider along with all the services loaded for this
     * Provider
     * 
     * @param provider
     * @return Provider with loaded services
     */
    @Cacheable("environments")
    public Provider getLoadedProvider(Provider provider) {
	Assert.notNull(provider, "Provider cannot be null");
	Assert.isTrue(getAllComponents().contains(provider),
		"No Provider [" + provider + "] mapped to any environment");
	for (Provider component : getAllComponents()) {
	    if (provider.equals(component))
		return component;
	}
	return null;
    }

    /**
     * Returns the loaded {@link Service}. A <b>Loaded</b> {@link Service} means
     * that the serviceTimeStatuses list of the service is initialised and contains
     * {@link ServiceTimeStatus} for each ping made to the Service.
     * @param service whose loaded Service is required
     * @return <b>Loaded</b> Service
     */
    public Service getLoadedService(Service service) {
	Assert.notNull(service, "Service cannot be null");
	Optional<Service> loadedService = getAllServices().stream()
		.filter(serviceFromLoadedServices -> serviceFromLoadedServices.equals(service)).findFirst();
	if (loadedService.isPresent())
	    return loadedService.get();
	else
	    throw new NoSuchElementException("Unable to find service " + service);
    }

    /**
     * Returns {@link Provider} corresponding to {@link Service}
     * @param service
     * @return Provider
     */
    public Provider getProviderForService(Service service) {
	Provider serviceProvider = new Provider(service.getProvider(), service.getEnvironment());
	Optional<Provider> provider = getAllComponents().stream().filter(component -> component.equals(serviceProvider))
		.findFirst();
	if (provider.isPresent())
	    return provider.get();
	else
	    throw new NoSuchElementException("No Provider Found for Service " + service);
    }

    /**
     * Gets Services of given {@link Provider}
     * 
     * @param component
     * @return Set of services
     */
    @Cacheable("environments")
    public Set<Service> getServicesByComponent(Provider component) {
	Assert.notNull(component, "Provider cannot be null");
	Assert.isTrue(getAllComponents().contains(component),
		"No Provider [" + component + "] mapped to any environment");

	for (Provider comp : getAllComponents()) {
	    if (comp.equals(component))
		return comp.getServices();
	}

	return null;
    }

    /**
     * Returns the Status for the Service by calling {@link #pingURL(String, int)}
     * @param service whose status is required
     * @return {@link ServiceStatus}
     */
    public ServiceStatus getStatusForService(Service service) {
	log.trace("Getting status for service {}", service);
	ServiceStatus status = pingURL(service.getUri(), coreConstants.connectionTimeoutInMillis) ? ServiceStatus.UP
		: ServiceStatus.DOWN;
	log.trace("Service is {}", status);
	return status;
    }

    /**
     * Pings a HTTP URL. This effectively sends a HEAD request and returns
     * <code>true</code> if the response code is in the 200-399 range.
     * 
     * @param url
     *            The HTTP URL to be pinged.
     * @param timeout
     *            The timeout in millis for both the connection timeout and the
     *            response read timeout. Note that the total timeout is
     *            effectively two times the given timeout.
     * @return <code>true</code> if the given HTTP URL has returned response
     *         code 200-399 on a HEAD request within the given timeout,
     *         otherwise <code>false</code>.
     */
    public boolean pingURL(String url, int timeout) {
	try {
	    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	    connection.setConnectTimeout(timeout);
	    connection.setReadTimeout(timeout);
	    connection.setRequestMethod("HEAD");
	    int responseCode = connection.getResponseCode();
	    return (200 <= responseCode && responseCode <= 399);
	} catch (IOException exception) {
	    return false;
	}
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	context = applicationContext;
    }

}
