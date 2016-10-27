package com.ds.ws.health.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.ds.ws.health.util.WSHealthUtils;


/**
 * Provider model.<br>
 * A Provider is supposed to have a name, version and name of {@link Environment} to which it belongs.<br>
 * A Provider can have zero or more {@link Service}
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 *
 */
public class Provider {
	
	private WSHealthUtils wsHealthUtils =  WSHealthUtils.instanceOf(WSHealthUtils.class);
	
	private final String environment;
	
	private final String name;
	
	private final String version;
	
	private Set<Service> services;
	
	private Status status;
	
	private Set<Service> downServices;
	
	public static final ComponentNameComparator COMPONENT_NAME_COMPARATOR = new ComponentNameComparator();
	
	public static enum Status {
		RED,
		AMBER,
		GREEN
	}
	
	public Provider(String name, String environment, String version) {
		Assert.isTrue(StringUtils.isNotBlank(name), "Provider name cannot be null or blank");
		Assert.isTrue(StringUtils.isNotBlank(environment), "Provider Environment name cannot be null or blank");
		Assert.isTrue(StringUtils.isNotBlank(version), "Provider version cannot be null or blank");
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
		//result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Provider other = (Provider) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		/*if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;*/
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		return true;
	}

	public final Set<Service> getServices() {
		if(null == services)
			services = new TreeSet<>(Service.SERVICE_DETAIL_COMPARATOR);
		return services;
	}

	public final void setServices(Set<Service> services) {
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
	
	/**
	 * Calculates and sets the status of the provider.
	 * @param services List of provider services. The size of the list shall correspond to<br>
	 * number of times the provider was hit before calculating the status
	 */
	public void setStatus(List<Set<Service>> services) {
		List<Status> statuses = new ArrayList<>();
		for (Set<Service> serviceSet : services)
			statuses.add(calculateStatus(serviceSet));
		
		this.status = computeStatus(statuses);
	}
	
	public Status getStatus() {
		return status;
	}

	/**
	 * Checks if given service is provided by this component
	 * @param service to be checked
	 * @return <code>True</code> is service is provided by component, <code>False</code> otherwise. 
	 */
	public boolean providesService(Service service) {
		return getServices().contains(service);
	}
	
	private Status calculateStatus(Set<Service> services) {
		Set<Service> downServices = getDownServices(new ArrayList<>(services));
		if(downServices.size() == 0)
			return Status.GREEN;
		else if(downServices.size() == getServices().size())
			return Status.RED;
		else return Status.AMBER;
	}
	
	private Status computeStatus(List<Status> statuses) {
		final int totalHits = statuses.size();
		int statusGreenCount = wsHealthUtils.countMatches(statuses, Status.GREEN);
		int statusRedCount = wsHealthUtils.countMatches(statuses, Status.RED);
		
		if(statusGreenCount == totalHits)
			return Status.GREEN;
		else if(statusRedCount > totalHits/2)
			return Status.RED;
		else
			return Status.AMBER;
	}

	/**
	 * Given list of services, sets the downservices
	 * @param services of this provider
	 */
	public void setDownServices(List<Service> services) {
		this.downServices = getDownServices(services);
	}
	
	public Set<Service> getDownServices() {
		return Collections.unmodifiableSet(this.downServices);
	}
	
	private Set<Service> getDownServices(List<Service> services) {
		Set<Service> downServices = new HashSet<>();
		for (Service service : services) {
			if(providesService(service) && service.isDown())
				downServices.add(service);
		}
		return downServices;
	}
	
	public boolean isDown() {
		return this.status.equals(Status.RED);
	}
	
	public boolean isUp() {
		return this.status.equals(Status.GREEN);
	}

	@Override
	public String toString() {
		return "Provider [environment=" + environment + ", name=" + name + ", version=" + version + ", services="
				+ services + ", status=" + status + "]";
	}

	private static class ComponentNameComparator implements Comparator<Provider> {

		@Override
		public int compare(Provider o1, Provider o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
}
