package com.ds.ws.health.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.core.EnvironmentLoader;
import com.ds.ws.health.model.Component;
import com.ds.ws.health.model.ComponentKey;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.ServiceDetail;
import com.ds.ws.health.scheduler.WSHealthReportGeneratorUtils;

@Service
@DependsOn(value="customTrustStoreManager")
public class WSHealthUtils {

	@Autowired
	@Qualifier("defaultEnvironmentLoader")
	private EnvironmentLoader environmentLoader;
	
	@Autowired
	private CoreConstants coreConstants;
	
	@Autowired
	private WorkbookUtils workbookUtils;
	
	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;

	@Cacheable(key="#root.methodName", value="environments")
	public Set<Environment> getAllEnvironments() {
		Set<Environment> environments = new HashSet<>();
		Map<String,Map<ComponentKey,Set<ServiceDetail>>> environmentDetails = environmentLoader.getEnvironmentDetails();
		for (Entry<String,Map<ComponentKey,Set<ServiceDetail>>> env : environmentDetails.entrySet()) {
			Environment environment = new Environment(env.getKey());
			for (Entry<ComponentKey,Set<ServiceDetail>> comp : env.getValue().entrySet()) {
				ComponentKey componentKey = comp.getKey();
				Component component = new Component(componentKey.getComponentName(),componentKey.getComponentEnv(),componentKey.getComponentVersion());
				for (ServiceDetail service : comp.getValue()) {
					component.getServices().add(service);
				}
				environment.getComponents().add(component);
			}
			environments.add(environment);
		}
		return Collections.unmodifiableSet(environments);
	}

	@Cacheable(key="#root.methodName", value="environments")
	public Set<Component> getAllComponents() {
		Set<Component> components = new HashSet<>();
		for (Environment environment : getAllEnvironments()) {
			components.addAll(environment.getComponents());
		}
		return Collections.unmodifiableSet(components);
	}

	@Cacheable(key="#root.methodName", value="environments")
	public Set<ServiceDetail> getAllServices() {
		Set<ServiceDetail> services = new HashSet<>();

		for (Environment environment : getAllEnvironments()) {
			for (Component component : environment.getComponents()) {
				services.addAll(component.getServices());
			}
		}

		return Collections.unmodifiableSet(services);
	}

	@Cacheable(key="#root.methodName", value="environments")
	public Set<Component> getComponentsByEnv(String envName) {
		Assert.isTrue(getAllEnvironments().contains(new Environment(envName)), "No environment with name ["+envName+"] mapped.");

		for (Environment environment : getAllEnvironments()) {
			if(environment.getName().equals(envName))
				return environment.getComponents();
		}

		return null;
	}

	@Cacheable(key="#root.methodName", value="environments")
	public Set<ServiceDetail> getServicesByComponent(Component component) {
		Assert.isTrue(getAllComponents().contains(component), "No Component ["+component+"] mapped to any environment");

		for (Component comp : getAllComponents()) {
			if(comp.equals(component))
				return comp.getServices();
		}

		return null;
	}

	public List<ServiceDetail> getServiceHealthDetails() {
		List<ServiceDetail> serviceDetails = new ArrayList<>(getAllServices());
		Collections.sort(serviceDetails, ServiceDetail.SERVICE_DETAIL_COMPARATOR);
		for (ServiceDetail serviceDetail : serviceDetails) {
			String serviceStatus = pingURL(serviceDetail.getUri(), coreConstants.connectionTimeoutInMillis)
					? coreConstants.serviceStatusPassed : coreConstants.serviceStatusFailed;
			serviceDetail.setStatus(serviceStatus);
		}
		return Collections.unmodifiableList(serviceDetails);
	}
	
	public List<Environment> getEnvHealthDetailsFromReport() {
		List<Environment> environmentDetails = new ArrayList<>(getAllEnvironments());
		Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
		
		// Load workbook and sheet
		Workbook workbook = workbookUtils.loadWorkbook(reportUtils.getReport());
		Sheet reportSheet = workbookUtils.loadWorksheet(workbook.getSheet("report"));
		
		final int rowEndIndex = reportSheet.getLastRowNum();
		final int rowStartIndex = rowEndIndex - (getAllServices().size()-1); // index based
		
		List<Row> rows = workbookUtils.getRowData(rowStartIndex, rowEndIndex);
		Set<ServiceDetail> serviceDetailsFromReport = new HashSet<>();
		
		for (Row row : rows) {
			ServiceDetail serviceDetail = new ServiceDetail(row.getCell(1).getStringCellValue(),
					row.getCell(2).getStringCellValue(), row.getCell(3).getStringCellValue(),
					row.getCell(4).getStringCellValue());
			serviceDetail.setStatus(row.getCell(5).getStringCellValue());
			serviceDetailsFromReport.add(serviceDetail);
		}
		
		for (Environment environmentDetail : environmentDetails) {
			for (Component component : environmentDetail.getComponents()) {
				for (ServiceDetail serviceDetail : component.getServices()) {
					for (ServiceDetail serviceDetailFromReport : serviceDetailsFromReport) {
						if(serviceDetail.equals(serviceDetailFromReport))
							serviceDetail.setStatus(serviceDetailFromReport.getStatus());
					}
				}
			}
		}
		
		return Collections.unmodifiableList(environmentDetails);
	}
	
	public List<Environment> getEnvHealthDetails() {
		List<Environment> environmentDetails = new ArrayList<>(getAllEnvironments());
		Collections.sort(environmentDetails, Environment.ENVIRONMENT_NAME_COMPARATOR);
		for (Environment environmentDetail : environmentDetails) {
			for (Component component : environmentDetail.getComponents()) {
				for (ServiceDetail serviceDetail : component.getServices()) {
					String status = pingURL(serviceDetail.getUri(), coreConstants.connectionTimeoutInMillis)
							? coreConstants.serviceStatusPassed : coreConstants.serviceStatusFailed;
					serviceDetail.setStatus(status);
				}
			}
		}
		return Collections.unmodifiableList(environmentDetails);
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
			return (200 <= responseCode && responseCode <= 399);
		} catch (IOException exception) {
			return false;
		}
	}

}
