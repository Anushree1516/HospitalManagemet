package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.LocationUpdateRequest;
import com.bloodbank.bloodbank.entity.LiveLocationTracking;

import java.util.List;
import java.util.Optional;

public interface LocationTrackingService {

    LiveLocationTracking updateUserLocation(Long userId, LocationUpdateRequest request);

    Optional<LiveLocationTracking> getLatestUserLocation(Long userId);

    Optional<LiveLocationTracking> getLatestAmbulanceLocation(Long ambulanceId);

    List<LiveLocationTracking> getRequestLocationHistory(Long bloodRequestId);

    List<LiveLocationTracking> getAmbulanceLocationHistory(Long ambulanceId);
}
