package com.ds.ws.health.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.common.ReportConstants;
import com.ds.ws.health.core.EnvironmentLoader;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.Service.ServiceStatus;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;

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
    private ReportConstants reportConstants;

    @Autowired
    @Qualifier("defaultEnvironmentLoader")
    private EnvironmentLoader environmentLoader;

    @Autowired
    private WorkbookUtils workbookUtils;

    @Autowired
    private WSHealthReportGeneratorUtils reportUtils;

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
     * Gets the loaded Provider along with all the services loaded for this
     * Provider
     * 
     * @param provider
     * @return Provider with loaded services
     */
    @Cacheable("environments")
    public Provider getProvider(Provider provider) {
	Assert.notNull(provider, "Provider cannot be null");
	Assert.isTrue(getAllComponents().contains(provider),
		"No Provider [" + provider + "] mapped to any environment");
	for (Provider component : getAllComponents()) {
	    if (provider.equals(component))
		return component;
	}
	return null;
    }

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
     * Gets list of service details from currently loaded report book <br>
     * via one of the {@link WorkbookUtils#loadWorkbook()} methods
     * 
     * @param rowStartIndex
     *            index of first service row
     * @param rowEndIndex
     *            index of last service row
     * @return List of {@link Service} for the corresponding row range
     */
    @Deprecated
    public List<Service> getServiceDetailsFromReport(int rowStartIndex, int rowEndIndex) {
	// Load current report workbook
	Workbook workbook = workbookUtils.loadWorkbook(reportUtils.getReport());
	return getServiceDetailsFromReport(workbook, rowStartIndex, rowEndIndex);
    }

    /**
     * Gets list of service details from report book
     * 
     * @param reportBook
     *            from which service details are to be retrieved
     * @param rowStartIndex
     *            index of first service row
     * @param rowEndIndex
     *            index of last service row
     * @return List of {@link Service} for the corresponding row range
     */
    @Deprecated
    public List<Service> getServiceDetailsFromReport(Workbook reportBook, int rowStartIndex, int rowEndIndex) {
	Assert.notNull(reportBook, "reportbook cannot be empty");
	Assert.isTrue(rowEndIndex > rowStartIndex, "rowEndIndex must be greater than rowStartIndex");
	log.debug("Row Start Index is {}", rowStartIndex);
	log.debug("Row End Index is {}", rowEndIndex);

	// load workbook
	workbookUtils.loadWorkbook(reportBook);
	// load report sheet
	Sheet reportSheet = workbookUtils.loadWorksheet(reportBook.getSheet(reportConstants.reportFileSheetName));

	workbookUtils.proceedIfWorksheetNotEmpty(reportSheet);

	List<Row> rows = workbookUtils.getRowData(rowStartIndex, rowEndIndex);
	log.debug("Rows Fetched = {}", rows.size());

	return convertRowsToService(rows);
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
     * Given list of services gives map of list containing set of services of
     * the all the provider
     * 
     * @param services
     *            list of services which may contain 'n' services of 'n'
     *            providers hit 'n' number of times.<br>
     *            This method transforms the supplied raw data into Map whose
     *            Key is the {@link Provider} and Value<br>
     *            is List containing Set of {@link Service}.<br>
     *            The size of the list corresponds to number of times the<br>
     *            Provider was hit.
     * @return Map of Provider as key and List of provider services as values
     */
    @Deprecated
    public Map<Provider, List<Set<Service>>> getServicesHealthAllComponent(List<Service> services) {
	Map<Provider, List<Set<Service>>> providerServicesHealthMap = new HashMap<>();
	for (Service service : services) {
	    // create provider from service
	    Provider provider = new Provider(service.getProvider(), service.getEnvironment());
	    if (providerServicesHealthMap.containsKey(provider)) {
		// if MAP contains provider, get services LIST
		List<Set<Service>> providerAllServices = providerServicesHealthMap.get(provider);
		// Get the last SET of LIST and check if it contains the service
		// in consideration
		Set<Service> serviceSet = providerAllServices.get(providerAllServices.size() - 1);
		// if SET contains service, create new SET and add to LIST
		if (serviceSet.contains(service)) {
		    Set<Service> newSet = new HashSet<>();
		    newSet.add(service);
		    providerAllServices.add(newSet);
		} else {
		    // if SET does not contain service, add service this SET
		    // itself
		    serviceSet.add(service);
		}
	    } else {
		// if MAP does NOT contain provider, add provider with LIST of
		// SET of services of this provider
		Set<Service> serviceSet = new HashSet<>();
		serviceSet.add(service);
		List<Set<Service>> providerAllServices = new ArrayList<>();
		providerAllServices.add(serviceSet);
		providerServicesHealthMap.put(provider, providerAllServices);
	    }
	}
	return Collections.unmodifiableMap(providerServicesHealthMap);
    }

    /**
     * Given list of services and a provider gives list containing set of
     * services of the given provider
     * 
     * @param services
     *            list of services, generally containing the proper service
     *            status after pinging
     * @param component
     *            whose services are to be retrieved
     * @return List of Set of {@link Service}. The size of the list corresponds
     *         to number iteration of<br>
     *         the given component was hit
     */
    @Deprecated
    public List<Set<Service>> getServicesHealthForProvider(List<Service> services, Provider component) {
	return getServicesHealthAllComponent(services).get(component);
    }

    public ServiceStatus getStatusForService(Service service) {
	log.debug("Getting status for service {}", service);
	ServiceStatus status = pingURL(service.getUri(), coreConstants.connectionTimeoutInMillis) ? ServiceStatus.UP
		: ServiceStatus.DOWN;
	log.debug("Service is {}", status);
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
	// url = url.replaceFirst("^https", "http"); // Otherwise an exception
	// may be thrown on invalid SSL certificates.

	try {
	    // Proxy proxy = new Proxy(Proxy.Type.HTTP, new
	    // InetSocketAddress(coreConstants.connectionProxyHost,coreConstants.connectionProxyPort));
	    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	    connection.setConnectTimeout(timeout);
	    connection.setReadTimeout(timeout);
	    connection.setRequestMethod("HEAD");
	    int responseCode = connection.getResponseCode();
	    log.debug("Response Code for URL {} is {}", url, responseCode);
	    return (200 <= responseCode && responseCode <= 399);
	} catch (IOException exception) {
	    return false;
	}
    }

    /*
     * public void setStatusForProvider(Provider provider) { //
     * setStatusForServices(provider.getServices());
     * provider.setDownServices(new ArrayList<>(provider.getServices()));
     * provider.setOverallStatus(getServicesHealthForProvider(new
     * ArrayList<>(provider.getServices()), provider)); }
     * 
     * public void setStatusForProviderFromReport(Provider provider,
     * List<Service> serviceDetailsFromReport) { //
     * setStatusForServicesFromReport(provider.getServices(), //
     * serviceDetailsFromReport);
     * provider.setDownServices(serviceDetailsFromReport);
     * provider.setOverallStatus(getServicesHealthForProvider(new
     * ArrayList<>(provider.getServices()), provider)); }
     * 
     * public void setStatusForProviders(Set<Provider> providers) {
     * providers.stream().forEach(provider -> setStatusForProvider(provider)); }
     * 
     * public void setStatusForProvidersFromReport(Set<Provider> providers,
     * List<Service> serviceDetailsFromReport) {
     * providers.stream().forEach(provider ->
     * setStatusForProviderFromReport(provider, serviceDetailsFromReport)); }
     */

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	context = applicationContext;
    }

    /*
     * public void setStatusForServices(Set<Service> services) {
     * services.stream().forEach(service -> setStatusForService(service)); }
     */

    /*
     * public void setStatusForServicesFromReport(Set<Service> services,
     * List<Service> serviceDetailsFromReport) {
     * services.stream().forEach(service -> { Optional<Service>
     * mathcedServiceFromReport = serviceDetailsFromReport.stream()
     * .filter(serviceFromReport ->
     * service.equals(serviceFromReport)).findFirst(); if
     * (mathcedServiceFromReport.isPresent()) service.setStatus(); }); }
     */

}
