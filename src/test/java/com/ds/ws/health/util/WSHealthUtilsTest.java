package com.ds.ws.health.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.ws.health.model.Component;

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
		assertTrue(wsHealthUtils.getAllComponents().size() == 12);
	}

	@Test
	public void testGetAllServices() {
		assertTrue(wsHealthUtils.getAllServices().size() == 12);
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

}
