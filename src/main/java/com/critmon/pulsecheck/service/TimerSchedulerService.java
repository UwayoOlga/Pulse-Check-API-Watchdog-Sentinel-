package com.critmon.pulsecheck.service;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class TimerSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(TimerSchedulerService.class);

    private final ScheduledExecutorService scheduler;
    private final Map<Long, ScheduledFuture<?>> activeTimers;

    public TimerSchedulerService() {
        this.scheduler = Executors.newScheduledThreadPool(10);
        this.activeTimers = new ConcurrentHashMap<>();
    }

    public void scheduleTimeout(Long deviceId, int timeoutSeconds, Runnable timeoutAction) {
        cancelTimer(deviceId);

        ScheduledFuture<?> timerTask = scheduler.schedule(
            timeoutAction,
            timeoutSeconds,
            TimeUnit.SECONDS
        );

        activeTimers.put(deviceId, timerTask);
        logger.info("Timer scheduled for device: {} with timeout: {}s", deviceId, timeoutSeconds);
    }

    public void cancelTimer(Long deviceId) {
        ScheduledFuture<?> existingTimer = activeTimers.remove(deviceId);
        if (existingTimer != null && !existingTimer.isDone()) {
            existingTimer.cancel(false);
            logger.info("Timer cancelled for device: {}", deviceId);
        }
    }

    public boolean hasActiveTimer(Long deviceId) {
        ScheduledFuture<?> timer = activeTimers.get(deviceId);
        return timer != null && !timer.isDone();
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down timer scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
