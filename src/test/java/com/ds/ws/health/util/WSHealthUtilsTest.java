package com.ds.ws.health.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/root-context.xml" })
public class WSHealthUtilsTest {

    @Autowired
    private WSHealthUtils wsHealthUtils;

    @Test
    public void testCleanUrl() {
	final String url_1 = "https://www.google.co.in/?gws_rd\\=ssl";
	final String url_2 = "https://www.google.co.in";
	assertTrue(wsHealthUtils.cleanUrl(url_2).equals(url_2));
	assertFalse(wsHealthUtils.cleanUrl(url_1).equals(url_1));
	assertFalse(wsHealthUtils.cleanUrl(url_1).contains("?"));
    }

    @Test
    public void testCountMatches() {
	List<Integer> randomInts = Arrays.asList(1, 2, 3, 4, 1, 1, 2, 3, 5, 6);
	List<String> randomString = Arrays.asList("hello", "hello", "world", "world", "world", "world", "hello");
	int count_1 = wsHealthUtils.countMatches(randomInts, 1);
	int count_2 = wsHealthUtils.countMatches(randomInts, 2);
	int count_hello = wsHealthUtils.countMatches(randomString, "hello");
	int count_world = wsHealthUtils.countMatches(randomString, "world");
	assertEquals(3, count_1);
	assertEquals(2, count_2);
	assertEquals(3, count_hello);
	assertEquals(4, count_world);
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
    public void testGetAllEnvironments() {
	assertTrue(wsHealthUtils.getAllEnvironments().size() == 4);
    }

    @Test
    public void testGetAllServices() {
	int serviceCount = 0;
	for (Environment environment : wsHealthUtils.getAllEnvironments()) {
	    for (Provider component : environment.getComponents()) {
		serviceCount += component.getServices().size();
	    }
	}
	assertTrue(wsHealthUtils.getAllServices().size() == serviceCount);
    }

    @Test
    public void testGetComponentsByEnv() {
	assertTrue(wsHealthUtils.getComponentsByEnv("UAT").size() == 3);
    }

    @Test
    public void testGetServicesByComponent() {
	Provider component = new Provider("Google", "UAT");
	assertTrue(wsHealthUtils.getServicesByComponent(component).size() == 1);
    }

    @Test
    public void testInstanceOf() {
	CoreConstants coreConstants = WSHealthUtils.instanceOf(CoreConstants.class);
	assertNotNull(coreConstants);
    }

    @Test
    public void testPingURL() {
	assertTrue(wsHealthUtils.pingURL("https://www.facebook.com/", 1000));
    }

}
