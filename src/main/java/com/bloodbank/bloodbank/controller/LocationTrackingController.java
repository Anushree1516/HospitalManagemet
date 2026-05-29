package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.LocationUpdateRequest;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.LiveLocationTracking;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.LocationTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationTrackingController {

    private final LocationTrackingService locationTrackingService;
    private final UserRepository userRepository;

    /**
     * PUT /api/v1/location/update
     * Update current user's live location
     * Called every 10 seconds from mobile app
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<LiveLocationTracking>> updateMyLocation(
            @Valid @RequestBody LocationUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        LiveLocationTracking tracking = locationTrackingService.updateUserLocation(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Location updated", tracking));
    }

    /**
     * GET /api/v1/location/user/{userId}
     * Get latest location of a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<LiveLocationTracking>> getUserLocation(
            @PathVariable Long userId) {

        LiveLocationTracking tracking = locationTrackingService
                .getLatestUserLocation(userId)
                .orElseThrow(() -> new RuntimeException("No location found for user"));
        return ResponseEntity.ok(ApiResponse.success("User location", tracking));
    }

    /**
     * GET /api/v1/location/ambulance/{ambulanceId}
     * Get latest location of an ambulance
     */
    @GetMapping("/ambulance/{ambulanceId}")
    public ResponseEntity<ApiResponse<LiveLocationTracking>> getAmbulanceLocation(
            @PathVariable Long ambulanceId) {

        LiveLocationTracking tracking = locationTrackingService
                .getLatestAmbulanceLocation(ambulanceId)
                .orElseThrow(() -> new RuntimeException("No location found for ambulance"));
        return ResponseEntity.ok(ApiResponse.success("Ambulance location", tracking));
    }

    /**
     * GET /api/v1/location/ambulance/{ambulanceId}/history
     * Get full location history of ambulance (for tracking on map)
     */
    @GetMapping("/ambulance/{ambulanceId}/history")
    public ResponseEntity<ApiResponse<List<LiveLocationTracking>>> getAmbulanceHistory(
            @PathVariable Long ambulanceId) {

        List<LiveLocationTracking> history = locationTrackingService
                .getAmbulanceLocationHistory(ambulanceId);
        return ResponseEntity.ok(ApiResponse.success("Ambulance location history", history));
    }

    /**
     * GET /api/v1/location/request/{requestId}/history
     * Get full location history for a blood request
     */
    @GetMapping("/request/{requestId}/history")
    public ResponseEntity<ApiResponse<List<LiveLocationTracking>>> getRequestHistory(
            @PathVariable Long requestId) {

        List<LiveLocationTracking> history = locationTrackingService
                .getRequestLocationHistory(requestId);
        return ResponseEntity.ok(ApiResponse.success("Request location history", history));
    }
}
