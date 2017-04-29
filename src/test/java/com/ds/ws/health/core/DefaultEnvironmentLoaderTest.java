package com.ds.ws.health.core;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class DefaultEnvironmentLoaderTest extends BaseTest {

    private EnvironmentLoader environmentLoader;
    private WSHealthUtils wsHealthUtils;

    @Before
    public void setUp() {
	environmentLoader = WSHealthUtils.instanceOf(EnvironmentLoader.class);
	wsHealthUtils = rootContext.getBean(WSHealthUtils.class);
    }

    @Test
    public void testEnvHirearchyFromUtils() {
	assertTrue("Environments in envLoader and healthUtils do not match", CollectionUtils
		.isEqualCollection(environmentLoader.getEnvironments(), wsHealthUtils.getAllEnvironments()));

	Collection<Provider> loadedProviders = environmentLoader.getEnvironments().stream()
		.map(Environment::getComponents).flatMap(Set::stream).collect(Collectors.toList());
	assertTrue("Providers loaded in envLoader and healthUtils do not match",
		CollectionUtils.isEqualCollection(loadedProviders, wsHealthUtils.getAllComponents()));

	Collection<Service> loadedService = environmentLoader.getEnvironments().stream().map(Environment::getComponents)
		.flatMap(Set::stream).map(Provider::getServices).flatMap(Set::stream).collect(Collectors.toList());
	assertTrue("Services loaded in envLoader and healthUtils do not match",
		CollectionUtils.isEqualCollection(loadedService, wsHealthUtils.getAllServices()));
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
