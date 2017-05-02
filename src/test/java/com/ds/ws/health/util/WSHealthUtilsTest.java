package com.ds.ws.health.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.common.CoreConstants;
import com.ds.ws.health.model.Environment;
import com.ds.ws.health.model.Provider;
import com.ds.ws.health.model.Service;
import com.ds.ws.health.model.Service.ServiceStatus;
import com.ds.ws.health.model.ServiceTimeStatus;

@RunWith(JUnit4.class)
public final class WSHealthUtilsTest extends BaseTest {

    private WSHealthUtils wsHealthUtils;
    private WorkbookUtils workbookUtils;

    @Before
    public void setUp() {
	wsHealthUtils = rootContext.getBean(WSHealthUtils.class);
	workbookUtils = WSHealthUtils.instanceOf(WorkbookUtils.class);
    }

    @Test
    public void testCleanUrl() {
	final String url_1 = "https://www.google.co.in/?gws_rd\\=ssl";
	final String url_2 = "https://www.google.co.in";
	assertTrue(wsHealthUtils.cleanUrl(url_2).equals(url_2));
	assertFalse(wsHealthUtils.cleanUrl(url_1).equals(url_1));
	assertFalse(wsHealthUtils.cleanUrl(url_1).contains("?"));
    }

    @Test
    public void testConvertRowsToService() {
	Workbook wb = workbookUtils.loadWorkbook();
	Sheet sheet = wb.createSheet();
	Row row = sheet.createRow(0);
	row.createCell(1).setCellValue("DEV");
	row.createCell(2).setCellValue("Dummy");
	row.createCell(3).setCellValue("Dummy Service");
	row.createCell(4).setCellValue("https://www.dummy123456789.com/");
	row.createCell(5).setCellValue("DOWN");

	List<Service> services = wsHealthUtils.convertRowsToService(Arrays.asList(row));
	services.stream().forEach(service -> assertNotNull("Service obtained cannot be null", service));
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

	// test after getAllServices call is cached.
	Service service = wsHealthUtils.getAllServices().stream().findAny().get();
	int pingCount = service.getServiceTimeStatuses().size();
	service.getServiceTimeStatuses().add(new ServiceTimeStatus(System.currentTimeMillis(), ServiceStatus.UP));
	int latestPingCount = service.getServiceTimeStatuses().size();
	assertTrue(pingCount < latestPingCount);
	Service latestService = wsHealthUtils.getAllServices().stream().filter(s -> s.equals(service)).findFirst()
		.get();
	assertTrue("Cached Service Object returned. Ping count not incremented.",
		latestPingCount == latestService.getServiceTimeStatuses().size());

    }

    @Test
    public void testGetComponentsByEnv() {
	assertTrue(wsHealthUtils.getComponentsByEnv("UAT").size() == 3);
    }

    @Test
    public void testGetLoadedProvider() {
	wsHealthUtils.getAllComponents().stream().map(provider -> wsHealthUtils.getLoadedProvider(provider))
		.map(Provider::getServices).flatMap(Set::stream).forEach(service -> testLoadedService(service));
    }

    @Test
    public void testGetLoadedService() {
	testLoadedServices(wsHealthUtils.getAllServices());
    }

    @Test
    public void testGetProviderForService() {
	wsHealthUtils.getAllServices().stream().map(service -> wsHealthUtils.getProviderForService(service))
		.forEach(provider -> {
		    assertNotNull("Provider for service cannot be null", provider);
		});
    }

    @Test
    public void testGetServicesByComponent() {
	Provider component = new Provider("Google", new Environment("UAT"));
	assertTrue(wsHealthUtils.getServicesByComponent(component).size() == 1);
    }

    @Test
    public void testGetStatusForService() {
	Provider provider = new Provider("Dummy", new Environment("DEV"));
	Service service = new Service(provider, "abc123");
	assertNotNull("Status must not be null", wsHealthUtils.getStatusForService(service));
	assertTrue("Status must be DOWN", wsHealthUtils.getStatusForService(service).equals(ServiceStatus.DOWN));
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

    private void testLoadedService(Service service) {
	assertNotNull("Loaded Service cannot be null", wsHealthUtils.getLoadedService(service));
	assertNotNull("Service Time Status Object cannot be null for loaded service",
		wsHealthUtils.getLoadedService(service).getServiceTimeStatuses());
	assertTrue("Service Times List Cannot be Null or Empty",
		CollectionUtils.isNotEmpty(service.getServiceTimeStatuses()));
    }

    private void testLoadedServices(Set<Service> services) {
	services.stream().forEach(service -> testLoadedService(service));
    }

}
