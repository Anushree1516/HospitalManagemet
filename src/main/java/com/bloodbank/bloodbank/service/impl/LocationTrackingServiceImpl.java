package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.LocationUpdateRequest;
import com.bloodbank.bloodbank.entity.BloodRequest;
import com.bloodbank.bloodbank.entity.LiveLocationTracking;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.bloodbank.repository.LiveLocationTrackingRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.LocationTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationTrackingServiceImpl implements LocationTrackingService {

    private final LiveLocationTrackingRepository locationTrackingRepository;
    private final UserRepository userRepository;
    private final BloodRequestRepository bloodRequestRepository;

    @Override
    @Transactional
    public LiveLocationTracking updateUserLocation(Long userId, LocationUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Also update user's stored location
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        userRepository.save(user);

        BloodRequest bloodRequest = null;
        if (request.getBloodRequestId() != null) {
            bloodRequest = bloodRequestRepository.findById(request.getBloodRequestId())
                    .orElse(null);
        }

        LiveLocationTracking tracking = LiveLocationTracking.builder()
                .user(user)
                .bloodRequest(bloodRequest)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .speedKmh(request.getSpeedKmh())
                .headingDegrees(request.getHeadingDegrees())
                .accuracyMeters(request.getAccuracyMeters())
                .address(request.getAddress())
                .trackingType(request.getTrackingType() != null ? request.getTrackingType() : "USER")
                .build();

        return locationTrackingRepository.save(tracking);
    }

    @Override
    public Optional<LiveLocationTracking> getLatestUserLocation(Long userId) {
        return locationTrackingRepository.findTopByUserIdOrderByRecordedAtDesc(userId);
    }

    @Override
    public Optional<LiveLocationTracking> getLatestAmbulanceLocation(Long ambulanceId) {
        return locationTrackingRepository.findTopByAmbulanceIdOrderByRecordedAtDesc(ambulanceId);
    }

    @Override
    public List<LiveLocationTracking> getRequestLocationHistory(Long bloodRequestId) {
        return locationTrackingRepository.findByBloodRequestIdOrderByRecordedAtDesc(bloodRequestId);
    }

    @Override
    public List<LiveLocationTracking> getAmbulanceLocationHistory(Long ambulanceId) {
        return locationTrackingRepository.findByAmbulanceIdOrderByRecordedAtDesc(ambulanceId);
    }
}
