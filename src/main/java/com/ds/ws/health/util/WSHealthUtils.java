package com.barclays.solveit.ws.health.util;

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
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.barclays.solveit.ws.health.common.ReportConstants;
import com.barclays.solveit.ws.health.core.EnvironmentLoader;
import com.barclays.solveit.ws.health.model.Environment;
import com.barclays.solveit.ws.health.model.Provider;
import com.barclays.solveit.ws.health.model.Service;
import com.barclays.solveit.ws.health.model.Service.Status;
import com.barclays.solveit.ws.health.report.WSHealthReportGeneratorUtils;

/**
 * Utility class for {@link Environment} , {@link Provider} , {@link Service} related info 
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@org.springframework.stereotype.Service
public class WSHealthUtils implements ApplicationContextAware {
	
	private static final Logger logger = LoggerFactory.getLogger(WSHealthUtils.class);
	
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
	
	/**
	 * @return Set of all {@link Environment}
	 */
	@Cacheable("environments")
	public Set<Environment> getAllEnvironments() {
		return Collections.unmodifiableSet(environmentLoader.getEnvironments());
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
	 * @return Set of all {@link Service} across all {@link Provider} across all {@link Environment}
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
	 * @param envName
	 * @return Set of Providers
	 */
	@Cacheable("environments")
	public Set<Provider> getComponentsByEnv(String envName) {
		Assert.isTrue(getAllEnvironments().contains(new Environment(envName)), "No environment with name ["+envName+"] mapped.");

		for (Environment environment : getAllEnvironments()) {
			if(environment.getName().equals(envName))
				return environment.getComponents();
		}

		return null;
	}

	/**
	 * Gets Services of given {@link Provider}
	 * @param component
	 * @return Set of services
	 */
	@Cacheable("environments")
	public Set<Service> getServicesByComponent(Provider component) {
		Assert.notNull(component, "Provider cannot be null");
		Assert.isTrue(getAllComponents().contains(component), "No Provider ["+component+"] mapped to any environment");

		for (Provider comp : getAllComponents()) {
			if(comp.equals(component))
				return comp.getServices();
		}

		return null;
	}
	
	/**
	 * Gets the loaded Provider along with all the services loaded for this Provider
	 * @param provider
	 * @return Provider with loaded services
	 */
	@Cacheable("environments")
	public Provider getProvider(Provider provider) {
		Assert.notNull(provider, "Provider cannot be null");
		Assert.isTrue(getAllComponents().contains(provider), "No Provider ["+provider+"] mapped to any environment");
		for (Provider component : getAllComponents()) {
			if(provider.equals(component)) return component;
		}
		return null;
	}

	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in 
	 * the 200-399 range.
	 * @param url The HTTP URL to be pinged.
	 * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
	 * the total timeout is effectively two times the given timeout.
	 * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
	 * given timeout, otherwise <code>false</code>.
	 */
	public boolean pingURL(String url, int timeout) {
		//url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

		try {
			//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(coreConstants.connectionProxyHost,coreConstants.connectionProxyPort));
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			logger.debug("Response Code for URL {} is {}", url, responseCode);
			return (200 <= responseCode && responseCode <= 399);
		} catch (IOException exception) {
			return false;
		}
	}
	
	/**
	 * Cleans the given URL string by removing request parameters from the URL.<br>
	 * This also removes the <b>?</b> preceding the request params
	 * @param url to be cleaned
	 * @return cleaned URL string after stripping the request params
	 */
	public String cleanUrl(String url) {
		Assert.notNull(url,"URL cannot be null");
		if(url.contains("?"))
			logger.debug("URL {} may contain request parameters. Stripping request params.", url);
		return StringUtils.substringBefore(url, "?");
	}
	
	/**
	 * Factory method to get an object of type T from the spring context
	 * @param type Class of object
	 * @return initialised object from spring context
	 */
	public static <T> T instanceOf(Class<T> type) {
		 return type.cast(context.getBean(type));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
	
	/**
	 * Given list of services and a provider gives list containing set of services of the given provider
	 * @param services list of services, generally containing the proper service status after pinging
	 * @param component whose services are to be retrieved
	 * @return List of Set of {@link Service}. The size of the list corresponds to number iteration of<br>
	 * the given component was hit
	 */
	public List<Set<Service>> getServicesHealthForProvider(List<Service> services, Provider component) {
		return getServicesHealthAllComponent(services).get(component);
	}
	
	/**
	 * Given list of services gives map of list containing set of services of the all the provider
	 * @param services list of services which may contain 'n' services of 'n' providers hit 'n' number of times.<br>
	 * This method transforms the supplied raw data into Map whose Key is the {@link Provider} and Value<br> is
	 * List containing Set of {@link Service}.<br> The size of the list corresponds to number of times the<br>
	 * Provider was hit.
	 * @return Map of Provider as key and List of provider services as values  
	 */
	public Map<Provider,List<Set<Service>>> getServicesHealthAllComponent(List<Service> services) {
		Map<Provider,List<Set<Service>>> providerServicesHealthMap = new HashMap<>();
		for (Service service : services) {
			// create provider from service
			Provider provider = new Provider(service.getProvider(), service.getEnvironment(), "NA");
			if(providerServicesHealthMap.containsKey(provider)) {
				// if MAP contains provider, get services LIST 
				List<Set<Service>> providerAllServices = providerServicesHealthMap.get(provider);
				// Get the last SET of LIST and check if it contains the service in consideration
				Set<Service> serviceSet = providerAllServices.get(providerAllServices.size()-1);
				// if SET contains service, create new SET and add to LIST
				if(serviceSet.contains(service)) {
					Set<Service> newSet = new HashSet<>();
					newSet.add(service);
					providerAllServices.add(newSet);
				} else {
					// if SET does not contain service, add service this SET itself
					serviceSet.add(service);
				}
			} else {
				// if MAP does NOT contain provider, add provider with LIST of SET of services of this provider
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
	 * Counts the number of times a particular object occurs in the collection
	 * @param inputCollection containing 0 or more occurrence of match object
	 * @param match Object to be checked in collection
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
	 * Converts the given row to a {@link Service} object
	 * @param row to convert to Service. Should not be null
	 * @return {@link Service}
	 */
	public Service convertRowToService(Row row) {
		Assert.notNull(row, "Row cannot be null");
		Service service = new Service(row.getCell(1).getStringCellValue(),
				row.getCell(2).getStringCellValue(), row.getCell(3).getStringCellValue(),
				row.getCell(4).getStringCellValue());
		service.setStatus(Status.valueOf(row.getCell(5).getStringCellValue()));
		return service;
	}
	
	/**
	 * Converts the rows to a {@link Service} object
	 * @param rows list of rows to convert to Service. Should not be null
	 * @return List of {@link Service}
	 */
	public List<Service> convertRowsToService(List<Row> rows) {
		List<Service> serviceDetailsFromReport = new ArrayList<>();
		for (Row row : rows) {
			if(null == row) continue;
			serviceDetailsFromReport.add(convertRowToService(row));
		}
		return serviceDetailsFromReport;
	}
	
	/**
	 * Gets list of service details from report book
	 * @param reportBook from which service details are to be retrieved
	 * @param rowStartIndex index of first service row
	 * @param rowEndIndex index of last service row
	 * @return List of {@link Service} for the corresponding row range
	 */
	public List<Service> getServiceDetailsFromReport(Workbook reportBook, int rowStartIndex, int rowEndIndex) {
		Assert.notNull(reportBook, "reportbook cannot be empty");
		Assert.isTrue(rowEndIndex > rowStartIndex, "rowEndIndex must be greater than rowStartIndex");
		logger.debug("Row Start Index is {}", rowStartIndex);
		logger.debug("Row End Index is {}", rowEndIndex);
		
		// load workbook
		workbookUtils.loadWorkbook(reportBook);
		// load report sheet
		Sheet reportSheet = workbookUtils.loadWorksheet(reportBook.getSheet(reportConstants.reportFileSheetName));

		workbookUtils.proceedIfWorksheetNotEmpty(reportSheet);

		List<Row> rows = workbookUtils.getRowData(rowStartIndex, rowEndIndex);
		logger.debug("Rows Fetched = {}",rows.size());

		return convertRowsToService(rows);
	}
	
	/**
	 * Gets list of service details from currently loaded report book <br> via one of the
	 * {@link WorkbookUtils#loadWorkbook()} methods
	 * @param rowStartIndex index of first service row
	 * @param rowEndIndex index of last service row
	 * @return List of {@link Service} for the corresponding row range
	 */
	public List<Service> getServiceDetailsFromReport(int rowStartIndex, int rowEndIndex) {
		// Load current report workbook
		Workbook workbook = workbookUtils.loadWorkbook(reportUtils.getReport());
		return getServiceDetailsFromReport(workbook, rowStartIndex, rowEndIndex);
	}
	
}
