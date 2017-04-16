package com.ds.ws.health.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.common.Status;
import com.ds.ws.health.model.Service.ServiceStatus;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public class ServiceTest extends BaseTest {

    private WSHealthUtils wsHealthUtils;

    @Before
    public void setUp() {
	wsHealthUtils = WSHealthUtils.instanceOf(WSHealthUtils.class);
    }

    @Test
    public final void testGetStatus() {
	Service service = new ArrayList<>(wsHealthUtils.getAllServices()).get(0);
	Provider provider = wsHealthUtils.getProviderForService(service);

	service.setServiceTimeStatusResponse(new ServiceTimeStatusResponse());
	IntStream.range(0, 10).forEach(i -> service.getServiceTimeStatusResponse().getServiceTimes()
		.add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.DOWN)));
	service.calculateOverallStatus();
	assertTrue("Status must be RED", service.getOverallStatus().equals(Status.RED));
	assertTrue("Provider Status must be RED", provider.getOverallStatus().equals(Status.RED));

	service.setServiceTimeStatusResponse(new ServiceTimeStatusResponse());
	IntStream.range(0, 4).forEach(i -> service.getServiceTimeStatusResponse().getServiceTimes()
		.add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.DOWN)));
	IntStream.range(0, 5).forEach(i -> service.getServiceTimeStatusResponse().getServiceTimes()
		.add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.UP)));
	service.calculateOverallStatus();
	assertTrue("Status must be AMBER", service.getOverallStatus().equals(Status.AMBER));
	assertTrue("Provider Status must be AMBER", provider.getOverallStatus().equals(Status.AMBER));

	service.setServiceTimeStatusResponse(new ServiceTimeStatusResponse());
	IntStream.range(0, 10).forEach(i -> service.getServiceTimeStatusResponse().getServiceTimes()
		.add(new ServiceTimeStatus(String.valueOf(System.currentTimeMillis()), ServiceStatus.UP)));
	service.calculateOverallStatus();
	assertTrue("Status must be GREEN", service.getOverallStatus().equals(Status.GREEN));
	assertTrue("Provider Status must be GREEN", provider.getOverallStatus().equals(Status.GREEN));
    }

}
