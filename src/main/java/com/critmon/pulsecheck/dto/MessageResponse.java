package com.critmon.pulsecheck.dto;

import java.time.LocalDateTime;

public class MessageResponse {

    private String message;
    private LocalDateTime timestamp;

    public MessageResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
