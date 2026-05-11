package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.exception.MonitorNotFoundException;
import com.critmon.pulsecheck.domain.Monitor;
import com.critmon.pulsecheck.domain.MonitorStatus;
import com.critmon.pulsecheck.repository.MonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private final MonitorRepository monitorRepository;
    private final TimerSchedulerService timerScheduler;
    private final AlertService alertService;

    public MonitorService(MonitorRepository monitorRepository,
                          TimerSchedulerService timerScheduler,
                          AlertService alertService) {
        this.monitorRepository = monitorRepository;
        this.timerScheduler = timerScheduler;
        this.alertService = alertService;
    }

    @Transactional
    public Monitor registerMonitor(int timeoutSeconds, String alertEmail) {
        Monitor monitor = new Monitor(timeoutSeconds, alertEmail);
        monitor = monitorRepository.save(monitor);
        startMonitoring(monitor);

        logger.info("Registered monitor with generated ID: {}", monitor.getId());
        return monitor;
    }

    @Transactional
    public void processHeartbeat(Long id) {
        Objects.requireNonNull(id, "ID is required");

        Monitor monitor = monitorRepository.findById(id)
            .orElseThrow(() -> new MonitorNotFoundException("Monitor not found: " + id));

        if (monitor.getStatus() == MonitorStatus.PAUSED) {
            logger.info("Resuming paused monitor: {}", id);
        }

        monitor.updateHeartbeat();
        monitor.setStatus(MonitorStatus.ACTIVE);
        monitorRepository.save(monitor);

        startMonitoring(monitor);
        logger.info("Heartbeat received for: {}", id);
    }

    @Transactional
    public void pauseMonitor(Long id) {
        Objects.requireNonNull(id, "ID is required");

        Monitor monitor = monitorRepository.findById(id)
            .orElseThrow(() -> new MonitorNotFoundException("Monitor not found: " + id));

        monitor.setStatus(MonitorStatus.PAUSED);
        monitorRepository.save(monitor);
        timerScheduler.cancelTimer(id);

        logger.info("Monitor paused: {}", id);
    }

    @Transactional(readOnly = true)
    public Optional<Monitor> getMonitor(Long id) {
        return id == null ? Optional.empty() : monitorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Monitor> getDownMonitors() {
        return monitorRepository.findByStatus(MonitorStatus.DOWN);
    }

    public void startMonitoring(Monitor monitor) {
        timerScheduler.scheduleTimeout(
            monitor.getId(),
            monitor.getTimeoutSeconds(),
            () -> handleTimeout(monitor.getId())
        );
    }

    @Transactional
    public void handleTimeout(Long id) {
        if (id == null) return;

        monitorRepository.findById(id).ifPresent(monitor -> {
            if (monitor.getStatus() == MonitorStatus.ACTIVE) {
                monitor.setStatus(MonitorStatus.DOWN);
                monitorRepository.save(monitor);
                alertService.fireAlert(monitor);
                logger.warn("Device timeout: {}", id);
            }
        });
    }
}
