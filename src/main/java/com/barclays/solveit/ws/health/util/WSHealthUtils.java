package com.barclays.solveit.ws.health.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.barclays.solveit.ws.health.common.CoreConstants;
import com.barclays.solveit.ws.health.core.ServiceLoader;
import com.barclays.solveit.ws.health.model.ServiceDetail;

@Component
public class WSHealthUtils {
	
	@Autowired
	@Qualifier("defaultServiceLoader")
	private ServiceLoader serviceLoader;
	
	@Autowired
	private CoreConstants coreConstants;
	
	public Set<ServiceDetail> getServiceHealthDetails() {
		Set<ServiceDetail> serviceDetails = serviceLoader.getServiceDetails();
		for (ServiceDetail serviceDetail : serviceDetails) {
			String serviceStatus = pingURL(serviceDetail.getUri(), coreConstants.connectionTimeoutInMillis)
					? coreConstants.serviceStatusPassed : coreConstants.serviceStatusFailed;
			serviceDetail.setStatus(serviceStatus);
		}
		return Collections.unmodifiableSet(serviceDetails);
	}
	
	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in 
	 * the 200-399 range.
	 * @param url The HTTP URL to be pinged.
	 * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
	 * the total timeout is effectively two times the given timeout.
	 * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
	 * given timeout, otherwise <code>false</code>.
	 * @author http://stackoverflow.com/questions/3584210/preferred-java-way-to-ping-an-http-url-for-availability
	 */
	public boolean pingURL(String url, int timeout) {
	    //url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

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
	
	
	
}
