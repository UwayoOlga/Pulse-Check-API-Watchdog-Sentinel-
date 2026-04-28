package com.critmon.pulsecheck.exception;

public class MonitorNotFoundException extends RuntimeException {
    public MonitorNotFoundException(String message) {
        super(message);
    }
}