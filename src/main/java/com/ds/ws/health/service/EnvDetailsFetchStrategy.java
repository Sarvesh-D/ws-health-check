package com.ds.ws.health.service;

import java.util.List;

import com.ds.ws.health.model.Environment;

/**
 * Abstract Strategy class for fetching env health details.<br>
 * The implementations must register the strategy class in EnvDetailsFetchMode.
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 19/09/2016
 * @version 1.0
 * @see EnvDetailsFetchMode
 */
public abstract class EnvDetailsFetchStrategy {

    /**
     * Fetches the env health details
     * 
     * @return Fetched env health details
     */
    abstract List<Environment> getEnvHealthDetails();

}
