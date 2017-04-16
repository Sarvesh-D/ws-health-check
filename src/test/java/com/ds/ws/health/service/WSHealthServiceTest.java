package com.ds.ws.health.service;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Function;

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

    private WSHealthService wsHealthService;
    private WSHealthUtils wsHealthUtils;

    @Before
    public void setUp() {
	wsHealthUtils = rootContext.getBean(WSHealthUtils.class);
	wsHealthService = WSHealthUtils.instanceOf(WSHealthService.class);
    }

    @Test
    public void testGetEnvHealthDetails() {
	int componentCount = 0;
	int serviceCount = 0;
	final List<Environment> environments = wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.REAL_TIME);

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

    @Test
    @Deprecated
    public void testGetEnvHealthDetailsFromReport() {
	int componentCount = 0;
	int serviceCount = 0;
	final List<Environment> environments = wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.NEAR_REAL_TIME);

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

    // @Test
    // TODO see how to run this test on Travis.
    @Deprecated
    public void testGetEnvHealthDetailsFromReportForDate() {
	final List<Environment> environments = wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.DAILY);

	int componentCount = environments.stream().map(env -> env.getComponents().size()).mapToInt(Integer::intValue)
		.sum();

	int serviceCount = environments.stream().map(env -> env.getComponents().stream())
		.map(providerStream -> providerStream.map(provider -> provider.getServices()))
		.flatMap(Function.identity()).mapToInt(services -> services.size()).sum();

	assertTrue(environments.size() == wsHealthUtils.getAllEnvironments().size());
	assertTrue(componentCount == wsHealthUtils.getAllComponents().size());
	assertTrue(serviceCount == wsHealthUtils.getAllServices().size());

    }

    @Test
    public void testGetServiceHealthDetails() {
	assertTrue(wsHealthService.getServiceHealthDetails().size() == wsHealthUtils.getAllServices().size());
    }

}
