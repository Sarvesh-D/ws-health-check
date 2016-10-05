package com.barclays.solveit.ws.health.model;

/**
 * Environment Details model to be used while capturing env details
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
public class EnvironmentDetail {
	
	private final String envName;
	private final String providerName;
	private final String providerVer;
	private final String serviceDesc;
	private final String serviceUri;
	
	private EnvironmentDetail(Builder builder) {
		this.envName = builder.envName;
		this.providerName = builder.providerName;
		this.providerVer = builder.providerVer;
		this.serviceDesc = builder.serviceDesc;
		this.serviceUri = builder.serviceUri;
	}
	
	public String getEnvName() {
		return envName;
	}

	public String getProviderName() {
		return providerName;
	}

	public String getProviderVer() {
		return providerVer;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public String getServiceUri() {
		return serviceUri;
	}
	
	@Override
	public String toString() {
		return "EnvironmentDetailsView [envName=" + envName + ", providerName=" + providerName + ", providerVer="
				+ providerVer + ", serviceDesc=" + serviceDesc + ", serviceUri=" + serviceUri + "]";
	}

	public static class Builder {
		
		private final String envName;
		private final String providerName;
		private final String serviceUri;
		
		private String providerVer;
		private String serviceDesc;
		
		public Builder(String envName, String providerName, String serviceUri) {
			this.envName = envName;
			this.providerName = providerName;
			this.serviceUri = serviceUri;
		}
		
		public Builder providerVer(String val) {providerVer = val; return this;}
		
		public Builder serviceDesc(String val) {serviceDesc = val; return this;}
		
		public EnvironmentDetail build() {
			return new EnvironmentDetail(this);
		}
		
	}


}
