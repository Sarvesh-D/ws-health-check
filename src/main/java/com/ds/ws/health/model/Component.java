package com.ds.ws.health.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

public class Component {
	
	private final String environment;
	
	private final String name;
	
	private final String version;
	
	private Set<ServiceDetail> services;
	
	public static final ComponentNameComparator COMPONENT_NAME_COMPARATOR = new ComponentNameComparator();
	
	public Component(String name, String environment, String version) {
		Assert.isTrue(StringUtils.isNotBlank(name), "Component name cannot be null or blank");
		Assert.isTrue(StringUtils.isNotBlank(environment), "Component Environment name cannot be null or blank");
		Assert.isTrue(StringUtils.isNotBlank(version), "Component version cannot be null or blank");
		this.name = name;
		this.environment = environment;
		this.version = version;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Component other = (Component) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		return true;
	}

	public final Set<ServiceDetail> getServices() {
		if(null == services)
			services = new TreeSet<>(ServiceDetail.SERVICE_DETAIL_COMPARATOR);
		return services;
	}

	public final void setServices(Set<ServiceDetail> services) {
		this.services = services;
	}

	public final String getName() {
		return name;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "Component [environment=" + environment + ", name=" + name + ", version=" + version + ", services="
				+ services + "]";
	}
	
	private static class ComponentNameComparator implements Comparator<Component> {

		@Override
		public int compare(Component o1, Component o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
}
