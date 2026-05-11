package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.domain.Monitor;
import com.critmon.pulsecheck.domain.MonitorStatus;
import com.critmon.pulsecheck.repository.MonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorRecoveryService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorRecoveryService.class);

    private final MonitorRepository monitorRepository;
    private final MonitorService monitorService;

    public MonitorRecoveryService(MonitorRepository monitorRepository, MonitorService monitorService) {
        this.monitorRepository = monitorRepository;
        this.monitorService = monitorService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void recoverActiveMonitors() {
        logger.info("Recovering active monitors from database...");

        List<Monitor> activeMonitors = monitorRepository.findByStatus(MonitorStatus.ACTIVE);

        for (Monitor monitor : activeMonitors) {
            try {
                monitorService.startMonitoring(monitor);
                logger.info("Recovered monitoring for device: {}", monitor.getId());
            } catch (Exception e) {
                logger.error("Failed to recover monitor for device: {}", monitor.getId(), e);
            }
        }

        logger.info("Monitor recovery complete. Recovered {} active monitors", activeMonitors.size());
    }
}