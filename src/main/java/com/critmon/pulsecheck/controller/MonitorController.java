package com.critmon.pulsecheck.controller;

import com.critmon.pulsecheck.dto.MessageResponse;
import com.critmon.pulsecheck.dto.MonitorRegistration;
import com.critmon.pulsecheck.model.Monitor;
import com.critmon.pulsecheck.service.MonitorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitors")
public class MonitorController {

    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> registerMonitor(@Valid @RequestBody MonitorRegistration registration) {
        monitorService.registerMonitor(
            registration.getId(),
            registration.getTimeout(),
            registration.getAlertEmail()
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new MessageResponse("Monitor created successfully for device: " + registration.getId()));
    }

    @PostMapping("/{id}/heartbeat")
    public ResponseEntity<MessageResponse> sendHeartbeat(@PathVariable String id) {
        monitorService.processHeartbeat(id);

        return ResponseEntity
            .ok(new MessageResponse("Heartbeat received for device: " + id));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<MessageResponse> pauseMonitor(@PathVariable String id) {
        monitorService.pauseMonitor(id);

        return ResponseEntity
            .ok(new MessageResponse("Monitor paused for device: " + id));
    }

    @GetMapping("/down")
    public ResponseEntity<List<Monitor>> getDownMonitors() {
        return ResponseEntity.ok(monitorService.getDownMonitors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Monitor> getMonitor(@PathVariable String id) {
        return monitorService.getMonitor(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
