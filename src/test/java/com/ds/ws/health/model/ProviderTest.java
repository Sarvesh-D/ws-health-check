package com.ds.ws.health.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
	Provider provider = wsHealthUtils.getLoadedProvider(new Provider("Google", new Environment("SIT")));
	Set<Service> services = provider.getServices(); // make shallow copy for
							// testing
	for (Service service : services) {
	    service.setServiceTimeStatuses(new ArrayList<>());
	    service.getServiceTimeStatuses().add(new ServiceTimeStatus(System.currentTimeMillis(), ServiceStatus.UP));
	    service.calculateOverallStatus();
	}
	provider.calculateOverallStatus();
	assertTrue(provider.getOverallStatus().equals(Status.GREEN));

	for (Service service : services) {
	    service.setServiceTimeStatuses(new ArrayList<>());
	    service.getServiceTimeStatuses().add(new ServiceTimeStatus(System.currentTimeMillis(), ServiceStatus.DOWN));
	    service.getServiceTimeStatuses().add(new ServiceTimeStatus(System.currentTimeMillis(), ServiceStatus.UP));
	    service.getServiceTimeStatuses().add(new ServiceTimeStatus(System.currentTimeMillis(), ServiceStatus.UP));
	    service.calculateOverallStatus();
	}
	provider.calculateOverallStatus();
	assertTrue(provider.getOverallStatus().equals(Status.AMBER));

	for (Service service : services) {
	    service.setServiceTimeStatuses(new ArrayList<>());
	    service.getServiceTimeStatuses().add(new ServiceTimeStatus(System.currentTimeMillis(), ServiceStatus.DOWN));
	    service.calculateOverallStatus();
	}

	provider.calculateOverallStatus();
	assertTrue(provider.getOverallStatus().equals(Status.RED));

    }

}
