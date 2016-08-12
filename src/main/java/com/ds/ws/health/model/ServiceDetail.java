package com.ds.ws.health.model;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * @author Sarvesh
 *
 */
public class ServiceDetail {
	
	private final String uri;
	
	private final String provider;

	private final String environment;

	private final String description;
	
	private String status;
	
	public static final ServiceDetailComaparator SERVICE_DETAIL_COMPARATOR = new ServiceDetailComaparator();

	public ServiceDetail(String environment, String provider, String description, String uri) {
		Assert.isTrue(StringUtils.isNotBlank(environment), "Service environment cannot be null or blank");
		Assert.isTrue(StringUtils.isNotBlank(provider), "Service provider cannot be null or blank");
		Assert.isTrue(StringUtils.isNotBlank(uri), "Service URI cannot be null or blank");
		this.environment = environment;
		this.provider = provider;
		this.description = description;
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceDetail other = (ServiceDetail) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		return true;
	}

	public final String getEnvironment() {
		return environment;
	}

	public String getProvider() {
		return provider;
	}

	public String getDescription() {
		return description;
	}

	public String getUri() {
		return uri;
	}
	
	public final String getStatus() {
		return status;
	}

	public final void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ServiceDetail [environment=" + environment + ", provider=" + provider + ", description=" + description
				+ ", uri=" + uri + ", status=" + status + "]";
	}
	
	private static class ServiceDetailComaparator implements Comparator<ServiceDetail> {

		@Override
		public int compare(ServiceDetail o1, ServiceDetail o2) {
			final String serviceDetailsLeft = o1.getEnvironment()+"#"+o1.getProvider()+"#"+o1.getDescription();
			final String serviceDetailsRight = o2.getEnvironment()+"#"+o2.getProvider()+"#"+o2.getDescription();
			return serviceDetailsLeft.compareTo(serviceDetailsRight);
		}
		
	}
	
}
