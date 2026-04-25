package com.critmon.pulsecheck.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitors")
public class Monitor {

    @Id
    private String deviceId;
    
    @Column(nullable = false)
    private int timeoutSeconds;
    
    @Column(nullable = false)
    private String alertEmail;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonitorStatus status;
    
    @Column(nullable = false)
    private LocalDateTime lastHeartbeat;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Monitor() {
    }

    public Monitor(String deviceId, int timeoutSeconds, String alertEmail) {
        this.deviceId = deviceId;
        this.timeoutSeconds = timeoutSeconds;
        this.alertEmail = alertEmail;
        this.status = MonitorStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.lastHeartbeat = LocalDateTime.now();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public String getAlertEmail() {
        return alertEmail;
    }

    public MonitorStatus getStatus() {
        return status;
    }

    public void setStatus(MonitorStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
