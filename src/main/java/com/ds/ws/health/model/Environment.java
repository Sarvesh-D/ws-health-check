package com.ds.ws.health.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Environment model.<br>
 * An Environment is considered to have a name and one or more {@link Provider}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString
public class Environment {

    private static class EnvirnomentDetailsNameComaparator implements Comparator<Environment> {

	@Override
	public int compare(Environment o1, Environment o2) {
	    return o1.getName().compareTo(o2.getName());
	}

    }

    public static final EnvirnomentDetailsNameComaparator ENVIRONMENT_NAME_COMPARATOR = new EnvirnomentDetailsNameComaparator();

    @Getter
    private final String name;

    @Setter
    private Set<Provider> components;

    public Set<Provider> getComponents() {
	if (null == components)
	    components = new TreeSet<>(Provider.COMPONENT_NAME_COMPARATOR);
	return components;
    }

}
