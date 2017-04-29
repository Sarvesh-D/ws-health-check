package com.ds.ws.health.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ds.ws.health.model.EnvironmentDetailsViewResponse;
import com.ds.ws.health.service.WSHealthServiceTest;
import com.ds.ws.health.spring.config.HealthCheckRootConfig;
import com.ds.ws.health.spring.config.HealthCheckServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HealthCheckRootConfig.class, HealthCheckServletContext.class })
@WebAppConfiguration
public class WSHealthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
	mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetEnvHealthDetails() throws Exception {
	MvcResult response = mvc.perform(get("/env/health")).andExpect(status().isOk()).andReturn();
	EnvironmentDetailsViewResponse jsonResponse = new ObjectMapper()
		.readValue(response.getResponse().getContentAsString(), EnvironmentDetailsViewResponse.class);
	WSHealthServiceTest.testEnvHealthDetails(jsonResponse.getEnvironments());

    }

    @Test
    public void testGetReportForService() throws Exception {
	mvc.perform(get("/service/health").param("env", "SIT").param("provider", "Google").param("uri",
		"https://www.google.co.in")).andExpect(status().isOk());
    }

}
