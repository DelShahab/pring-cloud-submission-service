package com.windsurf.agentportal.service.impl;

import com.windsurf.agentportal.dto.NotificationRequest;
import com.windsurf.agentportal.model.Notification;
import com.windsurf.agentportal.repository.NotificationRepository;
import com.windsurf.agentportal.service.NotificationService;
import com.windsurf.agentportal.service.base.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of NotificationService
 */
@Service
@Slf4j
public class NotificationServiceImpl extends BaseServiceImpl<Notification, String, NotificationRepository> 
        implements NotificationService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public NotificationServiceImpl(NotificationRepository repository) {
        super(repository);
    }
    
    @Override
    public Notification createNotification(String userId, NotificationRequest request) {
        log.debug("Creating notification for userId: {}, submissionId: {}", userId, request.getSubmissionId());
        
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .submissionId(request.getSubmissionId())
                .status(request.getStatus())
                .message(request.getMessage())
                .timestamp(LocalDateTime.parse(request.getTimestamp(), DATE_FORMATTER))
                .read(false)
                .build();
        
        Notification saved = repository.save(notification);
        log.info("Notification created with id: {} for user: {}", saved.getId(), userId);
        
        return saved;
    }
    
    @Override
    public List<Notification> getNotificationsByUserId(String userId) {
        log.debug("Fetching notifications for userId: {}", userId);
        return repository.findByUserId(userId);
    }
    
    @Override
    public List<Notification> getUnreadNotificationsByUserId(String userId) {
        log.debug("Fetching unread notifications for userId: {}", userId);
        return repository.findByUserIdAndReadFalse(userId);
    }
    
    @Override
    public void markAsRead(String notificationId) {
        log.debug("Marking notification as read: {}", notificationId);
        repository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            repository.save(notification);
            log.debug("Notification marked as read: {}", notificationId);
        });
    }
    
    @Override
    public void markAllAsRead(String userId) {
        log.debug("Marking all notifications as read for userId: {}", userId);
        List<Notification> unreadNotifications = repository.findByUserIdAndReadFalse(userId);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            repository.save(notification);
        });
        log.info("Marked {} notifications as read for userId: {}", unreadNotifications.size(), userId);
    }
}
