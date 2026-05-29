package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.BloodRequestDto;
import com.bloodbank.bloodbank.entity.BloodRequest;
import com.bloodbank.bloodbank.entity.Hospital;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.enums.RequestStatus;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.BloodRequestService;
import com.bloodbank.bloodbank.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodRequestServiceImpl implements BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BloodRequest createRequest(Long userId, BloodRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Hospital hospital = null;
        if (dto.getHospitalId() != null) {
            hospital = hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        }

        BloodRequest request = BloodRequest.builder()
                .requestedBy(user)
                .hospital(hospital)
                .bloodGroup(dto.getBloodGroup())
                .unitsRequired(dto.getUnitsRequired())
                .patientName(dto.getPatientName())
                .patientAge(dto.getPatientAge())
                .reason(dto.getReason())
                .isEmergency(dto.getIsEmergency() != null ? dto.getIsEmergency() : false)
                .status(RequestStatus.PENDING)
                .patientLatitude(dto.getPatientLatitude())
                .patientLongitude(dto.getPatientLongitude())
                .patientAddress(dto.getPatientAddress())
                .requiredBy(dto.getRequiredBy())
                .build();

        BloodRequest saved = bloodRequestRepository.save(request);

        // If emergency → send push notifications to nearby donors
        if (Boolean.TRUE.equals(saved.getIsEmergency())
                && saved.getPatientLatitude() != null) {

            String msg = String.format(
                    "🚨 Urgent! %s blood needed near you. Patient: %s. Contact hospital immediately.",
                    saved.getBloodGroup().getLabel(),
                    saved.getPatientName()
            );

            notificationService.sendEmergencyNotificationToDonors(
                    saved.getBloodGroup().name(),
                    saved.getPatientLatitude(),
                    saved.getPatientLongitude(),
                    20.0, // 20km radius
                    msg
            );
        }

        return saved;
    }

    @Override
    @Transactional
    public BloodRequest updateStatus(Long requestId, RequestStatus status) {
        BloodRequest request = getById(requestId);
        request.setStatus(status);

        if (status == RequestStatus.FULFILLED) {
            request.setFulfilledAt(LocalDateTime.now());
        }

        BloodRequest updated = bloodRequestRepository.save(request);

        // Notify requester
        String message = "Your blood request status updated to: " + status.name();
        notificationService.createNotification(
                updated.getRequestedBy().getId(),
                "Blood Request Update",
                message,
                "REQUEST_UPDATE",
                updated.getId()
        );

        // Push notification
        if (updated.getRequestedBy().getFcmToken() != null) {
            notificationService.sendPushNotification(
                    updated.getRequestedBy().getFcmToken(),
                    "Blood Request Update",
                    message
            );
        }

        return updated;
    }

    @Override
    public List<BloodRequest> getRequestsByUser(Long userId) {
        return bloodRequestRepository.findByRequestedById(userId);
    }

    @Override
    public List<BloodRequest> getRequestsByHospital(Long hospitalId) {
        return bloodRequestRepository.findByHospitalId(hospitalId);
    }

    @Override
    public List<BloodRequest> getEmergencyRequests() {
        return bloodRequestRepository.findByIsEmergencyTrueAndStatusOrderByCreatedAtDesc(
                RequestStatus.PENDING
        );
    }

    @Override
    public List<BloodRequest> getAllRequests() {
        return bloodRequestRepository.findAll();
    }

    @Override
    public BloodRequest getById(Long id) {
        return bloodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found: " + id));
    }
}
