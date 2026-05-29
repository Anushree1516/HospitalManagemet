package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.entity.Donor;
import com.bloodbank.bloodbank.entity.Notification;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.DonorRepository;
import com.bloodbank.bloodbank.repository.NotificationRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.NotificationService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final DonorRepository donorRepository;

    @Override
    public Notification createNotification(Long userId, String title, String message,
                                            String type, Long referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    @Async
    public void sendPushNotification(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) {
            logger.warn("FCM token is null, skipping push notification");
            return;
        }

        // Only send if Firebase is initialized
        if (FirebaseApp.getApps().isEmpty()) {
            logger.warn("Firebase not initialized. Skipping push: {}", title);
            return;
        }

        try {
            Message message = Message.builder()
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(fcmToken)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Push notification sent: {}", response);
        } catch (Exception e) {
            logger.error("❌ Failed to send push notification: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmergencyNotificationToDonors(String bloodGroup,
                                                    double latitude,
                                                    double longitude,
                                                    double radiusKm,
                                                    String message) {
        List<Donor> nearbyDonors = donorRepository.findNearbyAvailableDonors(
                bloodGroup, latitude, longitude, radiusKm
        );

        logger.info("🚨 Sending emergency notification to {} nearby donors for blood group {}",
                nearbyDonors.size(), bloodGroup);

        for (Donor donor : nearbyDonors) {
            // Save DB notification
            createNotification(
                    donor.getUser().getId(),
                    "🚨 Emergency Blood Request",
                    message,
                    "EMERGENCY",
                    null
            );

            // Send Firebase push
            if (donor.getUser().getFcmToken() != null) {
                sendPushNotification(
                        donor.getUser().getFcmToken(),
                        "🚨 Urgent Blood Needed!",
                        message
                );
            }
        }
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
}
