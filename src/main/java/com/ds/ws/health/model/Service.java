package com.barclays.solveit.ws.health.model;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * A Service model.<br>
 * Service is supposed to have a uri, desc, the name of its {@link Provider}, {@link Environment}.<br>
 * Service also has a status which can be either up or down
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 *
 */
public class Service {
	
	private final String uri;
	
	private final String provider;

	private final String environment;

	private String description;
	
	private Status status;
	
	public static final ServiceDetailComaparator SERVICE_DETAIL_COMPARATOR = new ServiceDetailComaparator();
	
	public static enum Status {
		UP,
		DOWN
	}
	
	public Service(String environment, String provider, String description, String uri) {
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
		Service other = (Service) obj;
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
	
	public final Status getStatus() {
		return status;
	}

	public final void setStatus(Status status) {
		this.status = status;
	}
	
	public boolean isUp() {
		return this.status.equals(Status.UP);
	}
	
	public boolean isDown() {
		return !isUp();
	}

	@Override
	public String toString() {
		return "Service [environment=" + environment + ", provider=" + provider + ", description=" + description
				+ ", uri=" + uri + ", status=" + status + "]";
	}
	
	private static class ServiceDetailComaparator implements Comparator<Service> {

		@Override
		public int compare(Service o1, Service o2) {
			final String serviceDetailsLeft = o1.getEnvironment()+"#"+o1.getProvider()+"#"+o1.getUri();
			final String serviceDetailsRight = o2.getEnvironment()+"#"+o2.getProvider()+"#"+o2.getUri();
			return serviceDetailsLeft.compareTo(serviceDetailsRight);
		}
		
	}
	
}
