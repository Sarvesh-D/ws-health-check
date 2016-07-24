package com.barclays.solveit.ws.health.model;

/**
 * @author Sarvesh
 *
 */
public class ServiceDetail {
	
	private final String environment;
	
	private final String provider;

	private final String description;
	
	private final String uri;
	
	private boolean status;

	public ServiceDetail(String environment, String provider, String description, String uri) {
		this.environment = environment;
		this.provider = provider;
		this.description = description;
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		return true;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
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

	@Override
	public String toString() {
		return "ServiceDetail [environment=" + environment + ", provider=" + provider + ", description=" + description
				+ ", uri=" + uri + ", status=" + status + "]";
	}
	
}
