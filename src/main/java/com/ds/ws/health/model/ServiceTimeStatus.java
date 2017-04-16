package com.ds.ws.health.model;

import com.ds.ws.health.model.Service.ServiceStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Immutable Class to hold service ping time and its status at that time
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 19/09/2016
 * @version 1.0
 */
@RequiredArgsConstructor
@Getter
@ToString
public class ServiceTimeStatus {

    private final long time;
    private final ServiceStatus status;

}