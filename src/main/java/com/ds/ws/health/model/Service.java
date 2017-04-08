package com.ds.ws.health.model;

import java.util.Comparator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Service model.<br>
 * Service is supposed to have a uri, desc, the name of its {@link Provider},
 * {@link Environment}.<br>
 * Service also has a status which can be either up or down
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "uri", "environment", "provider" })
@ToString
@Getter
public class Service {

    public static enum Status {
	UP, DOWN
    }

    private static class ServiceDetailComaparator implements Comparator<Service> {

	@Override
	public int compare(Service o1, Service o2) {
	    final String serviceDetailsLeft = o1.getEnvironment() + "#" + o1.getProvider() + "#" + o1.getUri();
	    final String serviceDetailsRight = o2.getEnvironment() + "#" + o2.getProvider() + "#" + o2.getUri();
	    return serviceDetailsLeft.compareTo(serviceDetailsRight);
	}

    }

    public static final ServiceDetailComaparator SERVICE_DETAIL_COMPARATOR = new ServiceDetailComaparator();

    private final String uri;

    private final String provider;

    private final String environment;

    private String description;

    @Setter
    private Status status;

    public Service(String environment, String provider, String description, String uri) {
	this(uri, provider, environment);
	this.description = description;
    }

    public boolean isDown() {
	return !isUp();
    }

    public boolean isUp() {
	return this.status.equals(Status.UP);
    }

}
