package com.ds.ws.health.model;

import lombok.Data;

/**
 * Immutable Class to hold service ping time and its status at that time
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 19/09/2016
 * @version 1.0
 */
@Data
public class ServiceTimeStatus {

    private final String time;
    private final String status;

}