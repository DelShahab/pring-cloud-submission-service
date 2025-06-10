package com.windsurf.agentportal.repository;

import com.windsurf.agentportal.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Notification entity
 */
@Repository
public interface NotificationRepository extends CrudRepository<Notification, String> {
    
    /**
     * Find notifications by user ID
     * @param userId User ID
     * @return List of notifications
     */
    List<Notification> findByUserId(String userId);
    
    /**
     * Find notifications by submission ID
     * @param submissionId Submission ID
     * @return List of notifications
     */
    List<Notification> findBySubmissionId(String submissionId);
    
    /**
     * Find unread notifications by user ID
     * @param userId User ID
     * @return List of unread notifications
     */
    List<Notification> findByUserIdAndReadFalse(String userId);
}
