package com.ds.ws.health.core;

import java.util.Set;

import javax.annotation.PostConstruct;

import com.ds.ws.health.model.Environment;


/**
 * Root interface for all EnvironmentLoaders
 * @see DefaultEnvironmentLoader
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
public interface EnvironmentLoader {
	
	/**
	 * Loads all the {@link Environment} present in the system.<br>
	 * See the {@link Environment} hierarchy for more detail.<br>
	 * The implementer can cache the call for performance reasons.<br>
	 * Also the implementer must call this during {@link PostConstruct} as well. 
	 */
	void loadEnvironments();
	
	/**
	 * Returns the Set of all {@link Environment} loaded from the system.<br>
	 * See the {@link Environment} hierarchy for more detail. 
	 * @return Set of Environments.
	 */
	Set<Environment> getEnvironments();
	
}
