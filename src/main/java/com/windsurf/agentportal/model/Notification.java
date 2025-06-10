package com.windsurf.agentportal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Notification entity representing user notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(name = "idx_notification_user_id", columnList = "userId"),
    @Index(name = "idx_notification_submission_id", columnList = "submissionId"),
    @Index(name = "idx_notification_read", columnList = "read")
})
public class Notification {
    
    @Id
    private String id;
    private String userId;
    private String submissionId;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    @Column(nullable = false)
    @Builder.Default
    private Boolean read = false;
}
