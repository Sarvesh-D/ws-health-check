package com.ds.ws.health.report;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class WSHealthReportGeneratorUtilsTest extends BaseTest {

    private WSHealthReportGeneratorUtils reportUtils;
    private Properties reportProperties;

    @Before
    public void setUp() {
	reportUtils = WSHealthUtils.instanceOf(WSHealthReportGeneratorUtils.class);
	reportProperties = rootContext.getBean("reportProperties", Properties.class);
    }
    
    @Test
    public void test() {
	
    }

}
