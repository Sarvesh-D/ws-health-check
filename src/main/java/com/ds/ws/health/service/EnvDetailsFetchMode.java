package com.ds.ws.health.service;

/**
 * Enum for different env health fetch strategies
 * @author G09633463
 * @since 19/09/2016
 * @version 1.0
 * @see EnvDetailsFetchStrategy 
 */
public enum EnvDetailsFetchMode {
	
	/**
	 * Fetches the env health details for the current instance of time.<br>
	 * Pings all the services of all environments to build the response resulting slow performance.
	 */
	REAL_TIME(RealTimeEnvDetails.class),
	
	/**
	 * Fetches the env health details for the most recent instance when the services were pinged.<br>
	 * Retrieves details of env health from a stored location (in memory) and builds the response.<br>
	 * Results best performance.
	 */
	NEAR_REAL_TIME(NearRealTimeEnvDetails.class),
	
	/**
	 * Fetches the env health details for the duration specified by hourly.mail.interval property.<br>
	 * Retrieves details of env health from a stored location (in memory) and builds the response.<br>
	 * Depending on the ping interval as configured by mail.interval property the number of records fetched<br>
	 * can grow exponentially and may have direct impact on performance
	 */
	HOURLY(HourlyEnvDetails.class),
	
	/**
	 * Fetches the env health details for the duration specified by daily.mail.interval property.<br>
	 * Retrieves details of env health from a stored location (in memory) and builds the response.<br>
	 * Depending on the ping interval as configured by mail.interval property the number of records fetched<br>
	 * can grow exponentially. Results in worst performance.
	 */
	DAILY(DailyEnvDetails.class);
	
	private Class<? extends EnvDetailsFetchStrategy> strategyClass;
	
	private EnvDetailsFetchMode(Class<? extends EnvDetailsFetchStrategy> strategyClass) {
		this.strategyClass =  strategyClass;
	}
	
	public Class<? extends EnvDetailsFetchStrategy> getStrategy() {
		return this.strategyClass;
	}

}
