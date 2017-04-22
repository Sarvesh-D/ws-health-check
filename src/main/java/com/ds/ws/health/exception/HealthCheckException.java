package com.ds.ws.health.exception;

import lombok.extern.slf4j.Slf4j;

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
