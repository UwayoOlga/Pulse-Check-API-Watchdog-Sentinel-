package com.critmon.pulsecheck.api;

import com.critmon.pulsecheck.dto.MessageResponse;
import com.critmon.pulsecheck.dto.MonitorRegistration;
import com.critmon.pulsecheck.domain.Monitor;
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
        Monitor monitor = monitorService.registerMonitor(
            registration.getTimeout(),
            registration.getAlertEmail()
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new MessageResponse("Monitor created successfully with ID: " + monitor.getId()));
    }

    @PostMapping("/{id}/heartbeat")
    public ResponseEntity<MessageResponse> sendHeartbeat(@PathVariable Long id) {
        monitorService.processHeartbeat(id);

        return ResponseEntity
            .ok(new MessageResponse("Heartbeat received for device: " + id));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<MessageResponse> pauseMonitor(@PathVariable Long id) {
        monitorService.pauseMonitor(id);

        return ResponseEntity
            .ok(new MessageResponse("Monitor paused for device: " + id));
    }

    @GetMapping("/down")
    public ResponseEntity<List<Monitor>> getDownMonitors() {
        return ResponseEntity.ok(monitorService.getDownMonitors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Monitor> getMonitor(@PathVariable Long id) {
        return monitorService.getMonitor(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
