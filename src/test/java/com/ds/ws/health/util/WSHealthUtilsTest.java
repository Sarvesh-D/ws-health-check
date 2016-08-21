package com.ds.ws.health.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.ws.health.model.Component;
import com.ds.ws.health.model.Environment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/root-context.xml")
public class WSHealthUtilsTest {
	
	@Autowired
	private WSHealthUtils wsHealthUtils;

	@Test
	public void testGetAllEnvironments() {
		assertTrue(wsHealthUtils.getAllEnvironments().size() == 4);
	}

	@Test
	public void testGetAllComponents() {
		int componentCount = 0;
		for (Environment environment : wsHealthUtils.getAllEnvironments()) {
			componentCount += environment.getComponents().size();
		}
		assertTrue(wsHealthUtils.getAllComponents().size() == componentCount);
	}

	@Test
	public void testGetAllServices() {
		int serviceCount = 0;
		for (Component component : wsHealthUtils.getAllComponents()) {
			serviceCount += component.getServices().size();
		}
		assertTrue(wsHealthUtils.getAllServices().size() == serviceCount);
	}

	@Test
	public void testGetComponentsByEnv() {
		assertTrue(wsHealthUtils.getComponentsByEnv("SIT").size() == 3);
	}

	@Test
	public void testGetServicesByComponent() {
		Component component = new Component("Google", "DEV", "1.0");
		assertTrue(wsHealthUtils.getServicesByComponent(component).size() == 1);
	}
	
	@Test
	public void testPingUrl() {
		final String url_1 = "https://www.google.com/";
		final String url_2 = "https://www.dummysite.com/";
		final int timeout = 1000;
		
		assertTrue(wsHealthUtils.pingURL(url_1, timeout));
		assertFalse(wsHealthUtils.pingURL(url_2, timeout));
	}

}
