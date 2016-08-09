package com.ds.ws.health.model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Environment {
	
	private final String name;

	private Set<Component> components;
	
	public Environment(String name) {
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
		Environment other = (Environment) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public final Set<Component> getComponents() {
		if(null == components)
			components = new HashSet<>();
		return components;
	}

	public final void setComponents(Set<Component> components) {
		this.components = components;
	}

	public final String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Environment [name=" + name + ", components=" + components + "]";
	}
	
	public static class EnvironmentDetailNameComparator implements Comparator<Environment> {

		@Override
		public int compare(Environment o1, Environment o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
	
}
