package com.ds.ws.health.core;

import java.util.Set;

import com.ds.ws.health.model.Environment;

/**
 * Root interface for all EnvironmentLoaders
 * 
 * @see DefaultEnvironmentLoader
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
public interface EnvironmentLoader {

    /**
     * Returns the Set of all {@link Environment} loaded from the system.<br>
     * See the {@link Environment} hierarchy for more detail.
     * 
     * @return Set of Environments.
     */
    Set<Environment> getEnvironments();

    /**
     * Loads all the {@link Environment} present in the system.<br>
     * See the {@link Environment} hierarchy for more detail.<br>
     * The implementer can cache the call for performance reasons.<br>
     */
    void loadEnvironments();

}
