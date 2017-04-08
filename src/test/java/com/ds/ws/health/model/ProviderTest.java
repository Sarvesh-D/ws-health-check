package com.ds.ws.health.model;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.ws.health.model.Service.Status;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/root-context.xml" })
public class ProviderTest {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Test
    public void testGetStatus() {
	Provider provider = wsHealthUtils.getProvider(new Provider("Google", "SIT"));
	final int totalServices = wsHealthUtils.getServicesByComponent(provider).size();
	for (Service service : provider.getServices()) {
	    service.setStatus(Status.UP);
	}
	provider.setStatus(Arrays.asList(provider.getServices()));
	assertTrue(provider.getStatus().equals(com.ds.ws.health.model.Provider.Status.GREEN));

	for (Service service : provider.getServices()) {
	    service.setStatus(Status.DOWN);
	}
	provider.setStatus(Arrays.asList(provider.getServices()));
	assertTrue(provider.getStatus().equals(com.ds.ws.health.model.Provider.Status.RED));

	int count = 0;
	for (Service service : provider.getServices()) {
	    service.setStatus(Status.DOWN);
	    count++;
	    if (count >= totalServices / 2)
		break;
	}
	provider.setStatus(Arrays.asList(provider.getServices()));
	assertTrue(provider.getStatus().equals(com.ds.ws.health.model.Provider.Status.RED));

    }

}
