package com.ds.ws.health.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.ws.health.model.Component;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/root-context.xml")
public class WSHealthServiceTest {

	@Autowired
	@Qualifier("wSHealthServiceImpl")
	private WSHealthService wsHealthService;

	@Autowired
	private WSHealthUtils wsHealthUtils;

	@Test
	public void testGetServiceHealthDetails() {
		assertTrue(wsHealthService.getServiceHealthDetails().size() == wsHealthUtils.getAllServices().size());
	}

	@Test
	public void testGetEnvHealthDetailsFromReport() {
		int envCount = 0;
		int componentCount = 0;
		int serviceCount = 0;
		for (Environment environment : wsHealthService.getEnvHealthDetailsFromReport()) {
			envCount ++;
			componentCount += environment.getComponents().size();
			for (Component component : environment.getComponents()) {
				serviceCount += component.getServices().size();
			}
		}
		assertTrue(envCount == wsHealthUtils.getAllEnvironments().size());
		assertTrue(componentCount == wsHealthUtils.getAllComponents().size());
		assertTrue(serviceCount == wsHealthUtils.getAllServices().size());
	}

	@Test
	public void testGetEnvHealthDetails() {
		int envCount = 0;
		int componentCount = 0;
		int serviceCount = 0;
		for (Environment environment : wsHealthService.getEnvHealthDetails()) {
			envCount ++;
			componentCount += environment.getComponents().size();
			for (Component component : environment.getComponents()) {
				serviceCount += component.getServices().size();
			}
		}
		assertTrue(envCount == wsHealthUtils.getAllEnvironments().size());
		assertTrue(componentCount == wsHealthUtils.getAllComponents().size());
		assertTrue(serviceCount == wsHealthUtils.getAllServices().size());
	}

}
