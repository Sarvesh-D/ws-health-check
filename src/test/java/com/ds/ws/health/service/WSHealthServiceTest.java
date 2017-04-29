package com.ds.ws.health.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class WSHealthServiceTest extends BaseTest {

    private static WSHealthUtils wsHealthUtils = WSHealthUtils.instanceOf(WSHealthUtils.class);
    private WSHealthService wsHealthService;

    public static void testEnvHealthDetails(List<Environment> environments) {
	int componentCount = 0;
	int serviceCount = 0;

	for (Environment environment : environments) {
	    componentCount += environment.getComponents().size();
	}

	for (Environment environment : environments) {
	    for (Provider component : environment.getComponents()) {
		serviceCount += component.getServices().size();
	    }
	}
	assertTrue(environments.size() == wsHealthUtils.getAllEnvironments().size());
	assertTrue(componentCount == wsHealthUtils.getAllComponents().size());
	assertTrue(serviceCount == wsHealthUtils.getAllServices().size());
    }

    @Before
    public void setUp() {
	wsHealthService = WSHealthUtils.instanceOf(WSHealthService.class);
    }

    @Test
    public void testGetEnvHealthDetails() {
	testEnvHealthDetails(wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.REAL_TIME));
    }

    @Test
    public void testGetReportForService() {
	wsHealthUtils.getAllServices().stream().forEach(service -> {
	    assertTrue("Service Status Times must not be empty",
		    CollectionUtils.isNotEmpty(service.getServiceTimeStatusResponse().getServiceTimes()));
	});
    }

    @Test
    public void testGetServiceHealthDetails() {
	assertTrue(wsHealthService.getServiceHealthDetails().size() == wsHealthUtils.getAllServices().size());
    }

}
