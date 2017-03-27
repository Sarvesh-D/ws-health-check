package com.ds.ws.health.model;

/**
 * Immutable Class to hold service ping time and its status at that time
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 19/09/2016
 * @version 1.0
 */
public class ServiceTimeStatus {

    private final String time;
    private final String status;

    public ServiceTimeStatus(String time, String status) {
	this.time = time;
	this.status = status;
    }

    public String getStatus() {
	return status;
    }

    public String getTime() {
	return time;
    }

    @Override
    public String toString() {
	return "Response [time=" + time + ", status=" + status + "]";
    }

}