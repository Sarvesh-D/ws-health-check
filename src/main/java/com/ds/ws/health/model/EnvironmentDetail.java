package com.ds.ws.health.model;

public class EnvironmentDetail {

	private final String envName;
	private final String compName;
	private final String compVer;
	private final String serviceDesc;
	private final String serviceUri;

	private EnvironmentDetail(Builder builder) {
		this.envName = builder.envName;
		this.compName = builder.compName;
		this.compVer = builder.compVer;
		this.serviceDesc = builder.serviceDesc;
		this.serviceUri = builder.serviceUri;
	}

	public static class Builder {

		private final String envName;
		private final String compName;
		private final String serviceUri;
		private String compVer;
		private String serviceDesc;

		public Builder(String envName, String compName, String serviceUri) {
			this.envName = envName;
			this.compName = compName;
			this.serviceUri = serviceUri;
		}

		public Builder compVer(String value) {this.compVer = value; return this;}

		public Builder serviceDesc(String value) {this.serviceDesc = value; return this;}

		public EnvironmentDetail build() {
			return new EnvironmentDetail(this);
		}

	}

	public final String getEnvName() {
		return envName;
	}

	public final String getCompName() {
		return compName;
	}

	public final String getCompVer() {
		return compVer;
	}

	public final String getServiceDesc() {
		return serviceDesc;
	}

	public final String getServiceUri() {
		return serviceUri;
	}

}
