package com.ds.ws.health.core;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class DefaultEnvironmentLoaderTest extends BaseTest {

    private EnvironmentLoader environmentLoader;

    @Before
    public void setUp() {
	environmentLoader = WSHealthUtils.instanceOf(EnvironmentLoader.class);
    }

    @Test
    public void testGetEnvironments() {
	Set<Environment> environments = environmentLoader.getEnvironments();
	assertTrue("No Environments Loaded", environments.size() > 0);
	assertTrue("No Service Providers Loaded",
		environments.stream().map(Environment::getComponents).flatMap(Set::stream).count() > 0);

	environments.stream().map(Environment::getComponents).forEach(providers -> {
	    assertTrue("No Service Provider Loaded for one or more Environments", providers.size() > 0);
	});

	assertTrue("No Services Loaded", environments.stream().map(Environment::getComponents).flatMap(Set::stream)
		.map(Provider::getServices).flatMap(Set::stream).count() > 0);

	environments.stream().map(Environment::getComponents).flatMap(Set::stream).map(Provider::getServices)
		.forEach(services -> {
		    assertTrue("No Service Loaded for one or more Providers", services.size() > 0);
		});
    }

}
