package com.barclays.solveit.ws.health.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

public class Environment {
	
	private final String name;

	private Set<Component> components;
	
	public static final EnvirnomentDetailsNameComaparator ENVIRONMENT_NAME_COMPARATOR = new EnvirnomentDetailsNameComaparator(); 
	
	public Environment(String name) {
		Assert.isTrue(StringUtils.isNotBlank(name), "Environment name cannot be null or blank");
		this.name = name;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
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

	public Set<Component> getComponents() {
		if(null == components)
			components = new TreeSet<>(Component.COMPONENT_NAME_COMPARATOR);
		return components;
	}

	public void setComponents(Set<Component> components) {
		this.components = components;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Environment [name=" + name + ", components=" + components + "]";
	}
	
	private static class EnvirnomentDetailsNameComaparator implements Comparator<Environment> {

		@Override
		public int compare(Environment o1, Environment o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}	
	
}
