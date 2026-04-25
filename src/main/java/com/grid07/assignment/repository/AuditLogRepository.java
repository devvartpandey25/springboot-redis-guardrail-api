package com.grid07.assignment.repository;

import com.grid07.assignment.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository for Audit log entity
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPostId(Long postId);
}
