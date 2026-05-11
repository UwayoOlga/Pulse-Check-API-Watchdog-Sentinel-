package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.domain.Monitor;
import com.critmon.pulsecheck.domain.MonitorStatus;
import com.critmon.pulsecheck.repository.MonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaleMonitorSweeperService {

    private static final Logger logger = LoggerFactory.getLogger(StaleMonitorSweeperService.class);

    private final MonitorRepository monitorRepository;
    private final AlertService alertService;
    private final TimerSchedulerService timerScheduler;

    public StaleMonitorSweeperService(MonitorRepository monitorRepository,
                                      AlertService alertService,
                                      TimerSchedulerService timerScheduler) {
        this.monitorRepository = monitorRepository;
        this.alertService = alertService;
        this.timerScheduler = timerScheduler;
    }

    @Scheduled(fixedDelayString = "${sweeper.interval.ms:60000}")
    @Transactional
    public void sweepStaleMonitors() {
        LocalDateTime now = LocalDateTime.now();
        List<Monitor> activeMonitors = monitorRepository.findByStatus(MonitorStatus.ACTIVE);

        int sweptCount = 0;
        for (Monitor monitor : activeMonitors) {
            LocalDateTime deadline = monitor.getLastHeartbeat().plusSeconds(monitor.getTimeoutSeconds());

            // If deadline passed and no live timer exists (e.g. after restart), trigger alert
            if (now.isAfter(deadline) && !timerScheduler.hasActiveTimer(monitor.getId())) {
                logger.warn("Sweeper detected stale monitor: {}", monitor.getId());
                
                monitor.setStatus(MonitorStatus.DOWN);
                monitorRepository.save(monitor);
                alertService.fireAlert(monitor);
                sweptCount++;
            }
        }

        if (sweptCount > 0) {
            logger.info("Sweeper transitioned {} monitors to DOWN", sweptCount);
        }
    }
}
