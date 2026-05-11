package com.critmon.pulsecheck.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitors")
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("device_id")
    private Long id;

    @Column(nullable = false)
    @JsonProperty("timeout_seconds")
    private int timeoutSeconds;

    @Column(nullable = false)
    @JsonProperty("alert_email")
    private String alertEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonitorStatus status;

    @Column(nullable = false)
    @JsonProperty("last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @Column(nullable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public Monitor() {
    }

    public Monitor(int timeoutSeconds, String alertEmail) {
        this.timeoutSeconds = timeoutSeconds;
        this.alertEmail = alertEmail;
        this.status = MonitorStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.lastHeartbeat = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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
