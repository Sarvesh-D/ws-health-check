package com.ds.ws.health.common;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.util.WSHealthUtils;

@RunWith(JUnit4.class)
public final class CoreConstantsTest extends BaseTest {

    private CoreConstants coreConstants;
    private ReportConstants reportConstants;

    @Before
    public void setUp() {
	coreConstants = WSHealthUtils.instanceOf(CoreConstants.class);
	reportConstants = WSHealthUtils.instanceOf(ReportConstants.class);
    }

    @Test
    public void testCoreConstantsRegistered() {
	assertTrue("Service Details Separator Key Not Initialised",
		StringUtils.isNotBlank(coreConstants.serviceDetailsSeparatorKey));
	assertTrue("Invalid Connection Timeout", coreConstants.connectionTimeoutInMillis >= 0);
	assertTrue("Invalid Ping Interval", coreConstants.pingIntervalInMins >= 0);
	assertTrue("Invalid Component Status Ping Count", coreConstants.componentStatusPingCount >= 0);
	assertTrue("Report File Path Not Set", StringUtils.isNotBlank(reportConstants.reportFilePath));
	assertTrue("Report File Name Not Set", StringUtils.isNotBlank(reportConstants.reportFileName));
	assertTrue("Report Sheet Name Not Set", StringUtils.isNotBlank(reportConstants.reportFileSheetName));
	assertTrue("Report Headers Not Set", StringUtils.isNotBlank(reportConstants.reportHeaders));
	assertTrue("Report Headers must be 6", StringUtils.split(reportConstants.reportHeaders, ",").length == 6);
	assertTrue("Report Footer Not Set", StringUtils.isNotBlank(reportConstants.reportFooter));
	assertTrue("Report Footer Link Not Set", StringUtils.isNotBlank(reportConstants.reportFooterLink));
    }

}
