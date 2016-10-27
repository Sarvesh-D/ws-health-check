package com.ds.ws.health.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

/**
 * EnvironmentDetailsViewResponse to be used as {@link ResponseEntity}
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 *
 */
public class EnvironmentDetailsViewResponse {

	private List<Environment> environments;

	public List<Environment> getEnvironments() {
		if(null == environments)
			environments = new ArrayList<>();
		return environments;
	}
	
}
