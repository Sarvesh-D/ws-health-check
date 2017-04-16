package com.ds.ws.health.model;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.common.Status;
import com.ds.ws.health.model.Service.ServiceStatus;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class ProviderTest extends BaseTest {

    private WSHealthUtils wsHealthUtils;

    @Before
    public void setUp() {
	wsHealthUtils = WSHealthUtils.instanceOf(WSHealthUtils.class);
    }

    @Test
    public void testGetStatus() {
	Provider provider = wsHealthUtils.getProvider(new Provider("Google", "SIT"));
	Set<Service> services = provider.getServices(); // make shallow copy for
							// testing
	for (Service service : services) {
	    service.setServiceTimeStatusResponse(new ServiceTimeStatusResponse());
	    service.getServiceTimeStatusResponse().getServiceTimes()
		    .add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.UP));
	    service.calculateOverallStatus();
	}
	provider.calculateOverallStatus();
	assertTrue(provider.getOverallStatus().equals(Status.GREEN));

	for (Service service : services) {
	    service.setServiceTimeStatusResponse(new ServiceTimeStatusResponse());
	    service.getServiceTimeStatusResponse().getServiceTimes()
		    .add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.DOWN));
	    service.getServiceTimeStatusResponse().getServiceTimes()
		    .add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.UP));
	    service.getServiceTimeStatusResponse().getServiceTimes()
		    .add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.UP));
	    service.calculateOverallStatus();
	}
	provider.calculateOverallStatus();
	assertTrue(provider.getOverallStatus().equals(Status.AMBER));

	for (Service service : services) {
	    service.setServiceTimeStatusResponse(new ServiceTimeStatusResponse());
	    service.getServiceTimeStatusResponse().getServiceTimes()
		    .add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.DOWN));
	    service.calculateOverallStatus();
	}

	provider.calculateOverallStatus();
	assertTrue(provider.getOverallStatus().equals(Status.RED));

    }

}
