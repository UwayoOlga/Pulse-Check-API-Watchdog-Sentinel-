package com.critmon.pulsecheck.repository;

import com.critmon.pulsecheck.domain.Monitor;
import com.critmon.pulsecheck.domain.MonitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    List<Monitor> findByStatus(MonitorStatus status);
}