package com.ds.ws.health.service;

/**
 * Enum for different env health fetch strategies
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 19/09/2016
 * @version 1.0
 * @see EnvDetailsFetchStrategy
 */
public enum EnvDetailsFetchMode {

    /**
     * Fetches the env health details for the current instance of time.<br>
     * Pings all the services of all environments to build the response
     * resulting slow performance.
     */
    REAL_TIME(RealTimeEnvDetails.class);

    private Class<? extends EnvDetailsFetchStrategy> strategyClass;

    private EnvDetailsFetchMode(Class<? extends EnvDetailsFetchStrategy> strategyClass) {
	this.strategyClass = strategyClass;
    }

    public Class<? extends EnvDetailsFetchStrategy> getStrategy() {
	return this.strategyClass;
    }

}
