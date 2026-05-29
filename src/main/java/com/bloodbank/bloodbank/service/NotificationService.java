package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.entity.Notification;

import java.util.List;

public interface NotificationService {

    // Save DB notification
    Notification createNotification(Long userId, String title, String message,
                                     String type, Long referenceId);

    // Send Firebase push to single device
    void sendPushNotification(String fcmToken, String title, String body);

    // Send emergency push to all nearby donors
    void sendEmergencyNotificationToDonors(String bloodGroup,
                                            double latitude,
                                            double longitude,
                                            double radiusKm,
                                            String message);

    List<Notification> getUserNotifications(Long userId);

    void markAsRead(Long notificationId);

    long getUnreadCount(Long userId);
}
