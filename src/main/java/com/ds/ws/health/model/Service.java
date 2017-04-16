package com.ds.ws.health.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * A Service model.<br>
 * Service is supposed to have a uri, desc, the name of its {@link Provider},
 * {@link Environment}.<br>
 * Service also has a overallStatus which can be either up or down
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "uri", "environment", "provider" })
@ToString(of = { "uri", "description", "provider", "environment", "status", "overallStatus" })
@Getter
@JsonIgnoreProperties(value = { "serviceTimeStatusResponse", "wsHealthUtils" })
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
	    final String serviceDetailsLeft = o1.getEnvironment() + "#" + o1.getProvider() + "#" + o1.getUri();
	    final String serviceDetailsRight = o2.getEnvironment() + "#" + o2.getProvider() + "#" + o2.getUri();
	    return serviceDetailsLeft.compareTo(serviceDetailsRight);
	}

    }

    public static final ServiceDetailComaparator SERVICE_DETAIL_COMPARATOR = new ServiceDetailComaparator();

    private WSHealthUtils wsHealthUtils = WSHealthUtils.instanceOf(WSHealthUtils.class);

    private final String environment;

    private final String provider;

    private final String uri;

    private String description;

    @Setter
    // this setter is only exposed for test purpose
    private ServiceTimeStatusResponse serviceTimeStatusResponse;

    @Getter
    private Status overallStatus;

    private ServiceStatus status;

    public Service(String environment, String provider, String uri, String description) {
	this(environment, provider, uri);
	this.description = description;
    }

    public Service(String environment, String provider, String uri, String description,
	    ServiceTimeStatusResponse serviceTimeStatusResponse) {
	this(environment, provider, uri, description);
	this.serviceTimeStatusResponse = serviceTimeStatusResponse;
    }

    /**
     * Should ONLY be called with each ping to re-calculate the Status. This
     * implies this method should only be called when WSHealthReportGenerator
     * pings the service according to set ping.interval property. Also, since
     * the overall status of service affects the status of its enclosing
     * provider, call to this method MUST trigger call to
     * {@link Provider#setOverallStatus(List)} in the end. TODO need to check if
     * overallStatus can be reseted after the file rollover occurs. This will
     * mean that Provider wont be able to calculate its Status, since it depends
     * on overallStatus of its services
     */
    public void calculateOverallStatus() {
	List<Status> statuses = serviceTimeStatusResponse.getServiceTimes().stream().map(ServiceTimeStatus::getStatus)
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

    public boolean isDown() {
	return this.overallStatus.equals(Status.RED);
    }

    public boolean isUp() {
	return this.overallStatus.equals(Status.GREEN);
    }

    public Service setStatus(ServiceStatus serviceStatus) {
	this.status = serviceStatus;
	return this;
    }

}
