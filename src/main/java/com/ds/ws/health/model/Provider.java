package com.ds.ws.health.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.ds.ws.health.util.WSHealthUtils;

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
public class Provider {

    public static enum Status {
	RED, AMBER, GREEN
    }

    private static class ComponentNameComparator implements Comparator<Provider> {

	@Override
	public int compare(Provider o1, Provider o2) {
	    return o1.getName().compareTo(o2.getName());
	}

    }

    public static final ComponentNameComparator COMPONENT_NAME_COMPARATOR = new ComponentNameComparator();

    private WSHealthUtils wsHealthUtils = WSHealthUtils.instanceOf(WSHealthUtils.class);

    private final String environment;

    private final String name;

    private Set<Service> services;

    private Status status;

    private Set<Service> downServices;

    public Provider(String name, String environment) {
	Assert.isTrue(StringUtils.isNotBlank(name), "Provider name cannot be null or blank");
	Assert.isTrue(StringUtils.isNotBlank(environment), "Provider Environment name cannot be null or blank");
	this.name = name;
	this.environment = environment;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Provider other = (Provider) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (environment == null) {
	    if (other.environment != null)
		return false;
	} else if (!environment.equals(other.environment))
	    return false;
	return true;
    }

    public Set<Service> getDownServices() {
	return Collections.unmodifiableSet(this.downServices);
    }

    public String getEnvironment() {
	return environment;
    }

    public final String getName() {
	return name;
    }

    public final Set<Service> getServices() {
	if (null == services)
	    services = new TreeSet<>(Service.SERVICE_DETAIL_COMPARATOR);
	return services;
    }

    public Status getStatus() {
	return status;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((environment == null) ? 0 : environment.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    public boolean isDown() {
	return this.status.equals(Status.RED);
    }

    public boolean isUp() {
	return this.status.equals(Status.GREEN);
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

    /**
     * Given list of services, sets the downservices
     * 
     * @param services
     *            of this provider
     */
    public void setDownServices(List<Service> services) {
	this.downServices = getDownServices(services);
    }

    public final void setServices(Set<Service> services) {
	this.services = services;
    }

    /**
     * Calculates and sets the status of the provider.
     * 
     * @param services
     *            List of provider services. The size of the list shall
     *            correspond to<br>
     *            number of times the provider was hit before calculating the
     *            status
     */
    public void setStatus(List<Set<Service>> services) {
	List<Status> statuses = services.stream().map(serviceSet -> calculateStatus(serviceSet))
		.collect(Collectors.toList());
	this.status = computeStatus(statuses);
    }

    @Override
    public String toString() {
	return "Provider [environment=" + environment + ", name=" + name + ", services=" + services + ", status="
		+ status + "]";
    }

    private Status calculateStatus(Set<Service> services) {
	Set<Service> downServices = getDownServices(new ArrayList<>(services));
	if (downServices.isEmpty())
	    return Status.GREEN;
	else if (downServices.size() == getServices().size())
	    return Status.RED;
	else
	    return Status.AMBER;
    }

    private Status computeStatus(List<Status> statuses) {
	final int totalHits = statuses.size();
	int statusGreenCount = wsHealthUtils.countMatches(statuses, Status.GREEN);
	int statusRedCount = wsHealthUtils.countMatches(statuses, Status.RED);

	if (statusGreenCount == totalHits)
	    return Status.GREEN;
	else if (statusRedCount > totalHits / 2)
	    return Status.RED;
	else
	    return Status.AMBER;
    }

    private Set<Service> getDownServices(List<Service> services) {
	return services.stream().filter(service -> providesService(service) && service.isDown())
		.collect(Collectors.toSet());
    }

}
