package com.windsurf.agentportal.service;

import com.windsurf.agentportal.dto.NotificationRequest;
import com.windsurf.agentportal.model.Notification;
import com.windsurf.agentportal.service.base.BaseService;

import java.util.List;

/**
 * Service interface for managing notifications
 */
public interface NotificationService extends BaseService<Notification, String> {

    /**
     * Create notification for a user
     * @param userId User ID
     * @param request Notification request
     * @return Created notification
     */
    Notification createNotification(String userId, NotificationRequest request);
    
    /**
     * Get notifications by user ID
     * @param userId User ID
     * @return List of notifications
     */
    List<Notification> getNotificationsByUserId(String userId);
    
    /**
     * Get unread notifications by user ID
     * @param userId User ID
     * @return List of unread notifications
     */
    List<Notification> getUnreadNotificationsByUserId(String userId);
    
    /**
     * Mark notification as read
     * @param notificationId Notification ID
     */
    void markAsRead(String notificationId);
    
    /**
     * Mark all notifications as read for user
     * @param userId User ID
     */
    void markAllAsRead(String userId);
}
