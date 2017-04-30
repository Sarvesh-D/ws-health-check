package com.ds.ws.health.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Root Exception class
 * 
 * @author <a href="mailto:sarvesh.dubey@cdk.com">Sarvesh Dubey</a>
 *
 * @since 30-04-2017
 * @version 1.0
 */
@Slf4j
public class HealthCheckException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public HealthCheckException() {
	this("Something went wrong...");
    }

    public HealthCheckException(String msg) {
	log.error(msg);
    }

}
