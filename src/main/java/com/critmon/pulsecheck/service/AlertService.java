package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.model.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    public void fireAlert(Monitor monitor) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("ALERT", "Device " + monitor.getDeviceId() + " is down!");
        alert.put("time", LocalDateTime.now());
        alert.put("email", monitor.getAlertEmail());
        alert.put("lastHeartbeat", monitor.getLastHeartbeat());

        logger.error("CRITICAL ALERT: {}", alert);
        System.out.println(alert);
    }
}
