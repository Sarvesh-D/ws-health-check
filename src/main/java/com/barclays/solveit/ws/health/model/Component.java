package com.barclays.solveit.ws.health.model;

import java.util.HashSet;
import java.util.Set;

public class Component {
	
	private final String name;
	
	private Set<ServiceDetail> services;
	
	public Component(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		return true;
	}

	public final Set<ServiceDetail> getServices() {
		if(null == services)
			services = new HashSet<>();
		return services;
	}

	public final void setServices(Set<ServiceDetail> services) {
		this.services = services;
	}

	public final String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Component [name=" + name + ", services=" + services + "]";
	}
	
}
