package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.exception.MonitorNotFoundException;
import com.critmon.pulsecheck.model.Monitor;
import com.critmon.pulsecheck.model.MonitorStatus;
import com.critmon.pulsecheck.repository.MonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private final MonitorRepository monitorRepository;
    private final TimerSchedulerService timerScheduler;
    private final AlertService alertService;

    public MonitorService(MonitorRepository monitorRepository, TimerSchedulerService timerScheduler, AlertService alertService) {
        this.monitorRepository = monitorRepository;
        this.timerScheduler = timerScheduler;
        this.alertService = alertService;
    }

    public Monitor registerMonitor(String deviceId, int timeoutSeconds, String alertEmail) {
        if (monitorRepository.existsById(deviceId)) {
            throw new IllegalArgumentException("Monitor already exists for device: " + deviceId);
        }

        Monitor monitor = new Monitor(deviceId, timeoutSeconds, alertEmail);
        monitor = monitorRepository.save(monitor);

        startMonitoring(monitor);

        logger.info("Monitor registered for device: {}", deviceId);
        return monitor;
    }

    public void processHeartbeat(String deviceId) {
        Monitor monitor = getMonitor(deviceId)
            .orElseThrow(() -> new MonitorNotFoundException("Monitor not found for device: " + deviceId));

        if (monitor.getStatus() == MonitorStatus.PAUSED) {
            monitor.setStatus(MonitorStatus.ACTIVE);
            logger.info("Monitor resumed from pause: {}", deviceId);
        }

        monitor.updateHeartbeat();
        monitor.setStatus(MonitorStatus.ACTIVE);
        monitorRepository.save(monitor);

        startMonitoring(monitor);

        logger.info("Heartbeat received for device: {}", deviceId);
    }

    public void pauseMonitor(String deviceId) {
        Monitor monitor = getMonitor(deviceId)
            .orElseThrow(() -> new MonitorNotFoundException("Monitor not found for device: " + deviceId));

        monitor.setStatus(MonitorStatus.PAUSED);
        monitorRepository.save(monitor);
        timerScheduler.cancelTimer(deviceId);

        logger.info("Monitor paused for device: {}", deviceId);
    }

    public Optional<Monitor> getMonitor(String deviceId) {
        return monitorRepository.findById(deviceId);
    }

    public void startMonitoring(Monitor monitor) {
        timerScheduler.scheduleTimeout(
            monitor.getDeviceId(),
            monitor.getTimeoutSeconds(),
            () -> handleTimeout(monitor.getDeviceId())
        );
    }

    public List<Monitor> getDownMonitors() {
        return monitorRepository.findByStatus(MonitorStatus.DOWN);
    }

    private void handleTimeout(String deviceId) {
        Optional<Monitor> monitorOpt = monitorRepository.findById(deviceId);
        if (monitorOpt.isPresent()) {
            Monitor monitor = monitorOpt.get();
            if (monitor.getStatus() == MonitorStatus.ACTIVE) {
                monitor.setStatus(MonitorStatus.DOWN);
                monitorRepository.save(monitor);
                alertService.fireAlert(monitor);
                logger.warn("Device timeout: {}", deviceId);
            }
        }
    }
}
