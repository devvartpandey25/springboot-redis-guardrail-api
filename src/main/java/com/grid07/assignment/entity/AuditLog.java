package com.grid07.assignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data // Lombok: getters/setters, etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder // Handy for tests, not using everywhere
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long postId;
    private String actionType;
    private LocalDateTime timestamp;
    private Long actorId;

}

