package com.barclays.solveit.ws.health.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

/**
 * EnvironmentDetailsResponse to be used as {@link ResponseEntity}
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 *
 */
public class EnvironmentDetailsResponse {

	private List<EnvironmentDetail> environments;

	public List<EnvironmentDetail> getEnvironments() {
		if(null == environments)
			environments = new ArrayList<>();
		return environments;
	}

}
