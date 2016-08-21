package com.ds.ws.health.model;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentView {
	
	private List<Environment> environments;

	public final List<Environment> getEnvironments() {
		if(null == environments)
			environments = new ArrayList<>();
		return environments;
	}
	
}
