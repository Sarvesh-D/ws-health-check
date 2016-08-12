package com.barclays.solveit.ws.health.model;

public class ComponentKey {
	
	private final String componentName;
	
	private final String componentVersion;
	
	private final String componentEnv;

	public ComponentKey(String componentName, String componentVersion, String componentEnv) {
		this.componentName = componentName;
		this.componentVersion = componentVersion;
		this.componentEnv = componentEnv;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((componentEnv == null) ? 0 : componentEnv.hashCode());
		result = prime * result + ((componentName == null) ? 0 : componentName.hashCode());
		result = prime * result + ((componentVersion == null) ? 0 : componentVersion.hashCode());
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
		ComponentKey other = (ComponentKey) obj;
		if (componentEnv == null) {
			if (other.componentEnv != null)
				return false;
		} else if (!componentEnv.equals(other.componentEnv))
			return false;
		if (componentName == null) {
			if (other.componentName != null)
				return false;
		} else if (!componentName.equals(other.componentName))
			return false;
		if (componentVersion == null) {
			if (other.componentVersion != null)
				return false;
		} else if (!componentVersion.equals(other.componentVersion))
			return false;
		return true;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getComponentVersion() {
		return componentVersion;
	}

	public String getComponentEnv() {
		return componentEnv;
	}

	@Override
	public String toString() {
		return "ComponentKey [componentName=" + componentName + ", componentVersion=" + componentVersion
				+ ", componentEnv=" + componentEnv + "]";
	}
	
}
