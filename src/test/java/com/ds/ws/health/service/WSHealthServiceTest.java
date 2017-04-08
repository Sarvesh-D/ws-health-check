package com.ds.ws.health.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Provider.Status;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/root-context.xml" })
public class WSHealthServiceTest {

    @Autowired
    @Qualifier("wSHealthServiceImpl")
    private WSHealthService wsHealthService;

    @Autowired
    private WSHealthUtils wsHealthUtils;

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

    @Test
    public void testGetEnvHealthDetailsFromReportForDate() {
	int componentCount = 0;
	int serviceCount = 0;
	final List<Environment> environments = wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.DAILY);

	for (Environment environment : environments) {
	    componentCount += environment.getComponents().size();
	}

	for (Environment environment : environments) {
	    for (Provider component : environment.getComponents()) {
		serviceCount += component.getServices().size();
		if (component.equals(new Provider("Google", "UAT")))
		    assertTrue(component.getStatus().equals(Status.RED));
	    }
	}
	assertTrue(environments.size() == wsHealthUtils.getAllEnvironments().size());
	assertTrue(componentCount == wsHealthUtils.getAllComponents().size());
	assertTrue(serviceCount == wsHealthUtils.getAllServices().size());

    }

    @Test
    public void testGetServiceHealthDetails() {
	assertTrue(wsHealthService.getServiceHealthDetails().size() == wsHealthUtils.getAllServices().size());
    }

}
