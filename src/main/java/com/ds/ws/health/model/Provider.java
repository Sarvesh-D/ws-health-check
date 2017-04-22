package com.ds.ws.health.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.ds.ws.health.common.Status;
import com.ds.ws.health.util.WSHealthUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Provider model.<br>
 * A Provider is supposed to have a name, version and name of
 * {@link Environment} to which it belongs.<br>
 * A Provider can have zero or more {@link Service}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "name", "environment" })
@ToString(of = { "name", "environment" })
@JsonIgnoreProperties(value = { "wsHealthUtils" })
public class Provider {

    private static class ComponentNameComparator implements Comparator<Provider> {

	@Override
	public int compare(Provider o1, Provider o2) {
	    return o1.getName().compareTo(o2.getName());
	}

    }

    public static final ComponentNameComparator COMPONENT_NAME_COMPARATOR = new ComponentNameComparator();

    private WSHealthUtils wsHealthUtils = WSHealthUtils.instanceOf(WSHealthUtils.class);

    @Getter
    private final String name;

    @Getter
    private final String environment;

    @Setter
    private Set<Service> services;

    @Getter
    private Status overallStatus;

    private Set<Service> downServices;

    /**
     * Calculates and sets the overallStatus of the provider.
     */
    public void calculateOverallStatus() {
	List<Status> statuses = this.services.stream().map(Service::getOverallStatus).collect(Collectors.toList());
	final int totalStatuses = statuses.size();
	int statusGreenCount = wsHealthUtils.countMatches(statuses, Status.GREEN);
	int statusRedCount = wsHealthUtils.countMatches(statuses, Status.RED);
	if (statusGreenCount == totalStatuses)
	    this.overallStatus = Status.GREEN;
	else if (statusRedCount > totalStatuses / 2)
	    this.overallStatus = Status.RED;
	else
	    this.overallStatus = Status.AMBER;
    }

    /**
     * @return the Set of down services of {@link Provider}
     */
    public Set<Service> getDownServices() {
	return Collections.unmodifiableSet(this.downServices);
    }

    public final Set<Service> getServices() {
	if (null == services)
	    services = new TreeSet<>(Service.SERVICE_DETAIL_COMPARATOR);
	return services;
    }

    public boolean isDown() {
	return this.overallStatus.equals(Status.RED);
    }

    public boolean isUp() {
	return this.overallStatus.equals(Status.GREEN);
    }

    /**
     * Checks if given service is provided by this component
     * 
     * @param service
     *            to be checked
     * @return <code>True</code> is service is provided by component,
     *         <code>False</code> otherwise.
     */
    public boolean providesService(Service service) {
	return getServices().contains(service);
    }

    public Provider setDownServices() {
	this.downServices = services.stream().filter(Service::isDown).collect(Collectors.toSet());
	return this;
    }

}
