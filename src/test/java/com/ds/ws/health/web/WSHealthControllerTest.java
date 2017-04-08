package com.ds.ws.health.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/root-context.xml",
	"classpath:spring/appServlet/servlet-context.xml" })
@WebAppConfiguration*/
public class WSHealthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
	mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // @Test
    public void testGetEnvHealthDetails() throws Exception {
	mvc.perform(get("/env/health")).andExpect(status().isOk());
    }

    // @Test
    public void testGetReportForService() throws Exception {
	mvc.perform(get("/service/health").param("env", "SIT").param("provider", "GOOGLE").param("uri",
		"https://www.google.co.in")).andExpect(status().isOk());
    }

}
