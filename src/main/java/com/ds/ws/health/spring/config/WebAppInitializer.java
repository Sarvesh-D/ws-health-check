package com.ds.ws.health.spring.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
	// Create the 'root' Spring application context
	AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
	rootContext.register(HealthCheckRootConfig.class);

	// Manage the lifecycle of the root application context
	servletContext.addListener(new ContextLoaderListener(rootContext));

	// Create the dispatcher servlet's Spring application context
	AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
	dispatcherContext.register(HealthCheckServletContext.class);

	// Register and map the dispatcher servlet
	ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
		new DispatcherServlet(dispatcherContext));
	dispatcher.setLoadOnStartup(1);
	dispatcher.addMapping("/");

	servletContext.addFilter("CharacterEncodingFilter", CharacterEncodingFilter.class)
		.addMappingForUrlPatterns(null, true, "/*");
	Map<String, String> charEncodingFilterInitParams = new HashMap<>();
	charEncodingFilterInitParams.put("encoding", "UTF-8");
	charEncodingFilterInitParams.put("forceEncoding", "true");
	servletContext.getFilterRegistration("CharacterEncodingFilter").setInitParameters(charEncodingFilterInitParams);

    }

}
