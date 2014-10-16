package com.edifecs.servicemanager.metric.api.exception;

/**
 * Created by abhising on 17-06-2014.
 */
public class ZabbixItemAlreadyExistsException extends ZabbixApiException {

    public ZabbixItemAlreadyExistsException(String message) {
        super(message);
    }
}
