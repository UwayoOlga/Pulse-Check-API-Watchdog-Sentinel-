package com.critmon.pulsecheck.repository;

import com.critmon.pulsecheck.model.Monitor;
import com.critmon.pulsecheck.model.MonitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, String> {

    List<Monitor> findByStatus(MonitorStatus status);

    @Query("SELECT m FROM Monitor m WHERE m.status = 'ACTIVE' AND m.lastHeartbeat < :cutoffTime")
    List<Monitor> findStaleActiveMonitors(LocalDateTime cutoffTime);
}