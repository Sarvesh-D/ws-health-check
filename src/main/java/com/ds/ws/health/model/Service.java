package com.ds.ws.health.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ds.ws.health.common.Status;
import com.ds.ws.health.util.WSHealthUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Service model.<br>
 * Service is supposed to have a uri, desc, {@link Provider#getName()},
 * {@link Environment#getName()}.<br>
 * Service has a status which can be one of {@link ServiceStatus} Service also
 * has a overallStatus which can be either one of {@link Status}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "uri", "provider" })
@ToString(of = { "uri", "description", "provider", "status", "overallStatus" })
@Getter
@JsonIgnoreProperties(value = { "serviceTimeStatuses", "wsHealthUtils" })
public class Service {

    public enum ServiceStatus {

	UP, DOWN;

	private static Map<ServiceStatus, Status> serviceStatusToStatusMap = new HashMap<>();

	static {
	    serviceStatusToStatusMap.put(UP, Status.GREEN);
	    serviceStatusToStatusMap.put(DOWN, Status.RED);
	}

	public Status getStatusForServiceStatus(ServiceStatus serviceStatus) {
	    return ServiceStatus.serviceStatusToStatusMap.get(serviceStatus);
	}
    }

    private static class ServiceDetailComaparator implements Comparator<Service> {

	@Override
	public int compare(Service o1, Service o2) {
	    final String serviceDetailsLeft = o1.getProvider().getEnvironment().getName() + "#"
		    + o1.getProvider().getName() + "#" + o1.getUri();
	    final String serviceDetailsRight = o2.getProvider().getEnvironment().getName() + "#"
		    + o2.getProvider().getName() + "#" + o2.getUri();
	    return serviceDetailsLeft.compareTo(serviceDetailsRight);
	}

    }

    public static final ServiceDetailComaparator SERVICE_DETAIL_COMPARATOR = new ServiceDetailComaparator();

    private WSHealthUtils wsHealthUtils = WSHealthUtils.instanceOf(WSHealthUtils.class);

    private final Provider provider;

    private final String uri;

    private String description;

    @Setter
    private List<ServiceTimeStatus> serviceTimeStatuses;

    @Getter
    private Status overallStatus;

    private ServiceStatus status;

    public Service(Provider provider, String uri, String description) {
	this(provider, uri);
	this.description = description;
    }

    public Service(Provider provider, String uri, String description, List<ServiceTimeStatus> serviceTimeStatuses) {
	this(provider, uri, description);
	this.serviceTimeStatuses = serviceTimeStatuses;
    }

    /**
     * Calculates and sets the overall status of the Service. Should ONLY be
     * called with each ping to re-calculate the Status. This implies this
     * method should only be called when WSHealthReportGenerator pings the
     * service according to set ping.interval property. Also, since the overall
     * status of service affects the status of its enclosing provider, call to
     * this method MUST trigger call to
     * {@link Provider#calculateOverallStatus()} in the end.
     */
    public void calculateOverallStatus() {
	List<Status> statuses = serviceTimeStatuses.stream().map(ServiceTimeStatus::getStatus)
		.map(serviceStatus -> serviceStatus.getStatusForServiceStatus(serviceStatus))
		.collect(Collectors.toList());
	final int totalHits = statuses.size();
	int statusUpCount = wsHealthUtils.countMatches(statuses, Status.GREEN);
	int statusDownCount = wsHealthUtils.countMatches(statuses, Status.RED);

	if (statusUpCount == totalHits)
	    this.overallStatus = Status.GREEN;
	else if (statusDownCount > totalHits / 2)
	    this.overallStatus = Status.RED;
	else
	    this.overallStatus = Status.AMBER;

	// re-calculate provider's overall status
	wsHealthUtils.getProviderForService(this).setDownServices().calculateOverallStatus();
    }

    @JsonIgnore
    public boolean isDown() {
	return this.overallStatus.equals(Status.RED);
    }

    @JsonIgnore
    public boolean isUp() {
	return this.overallStatus.equals(Status.GREEN);
    }

    public Service setStatus(ServiceStatus serviceStatus) {
	this.status = serviceStatus;
	return this;
    }

}
