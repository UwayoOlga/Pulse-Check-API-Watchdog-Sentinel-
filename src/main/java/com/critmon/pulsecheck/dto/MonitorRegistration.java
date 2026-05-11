package com.critmon.pulsecheck.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class MonitorRegistration {

    @Min(value = 1, message = "Timeout must be at least 1 second")
    private int timeout;

    @NotBlank(message = "Alert email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("alert_email")
    private String alertEmail;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getAlertEmail() {
        return alertEmail;
    }

    public void setAlertEmail(String alertEmail) {
        this.alertEmail = alertEmail;
    }
}
