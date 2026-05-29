package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.AmbulanceRequest;
import com.bloodbank.bloodbank.dto.request.LocationUpdateRequest;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.Ambulance;
import com.bloodbank.bloodbank.enums.AmbulanceStatus;
import com.bloodbank.bloodbank.service.AmbulanceService;
import com.bloodbank.bloodbank.util.LocationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ambulances")
@RequiredArgsConstructor
public class AmbulanceController {

    private final AmbulanceService ambulanceService;
    private final LocationUtil locationUtil;

    /**
     * POST /api/v1/ambulances
     * Register a new ambulance (Hospital/Admin only)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<Ambulance>> registerAmbulance(
            @Valid @RequestBody AmbulanceRequest request) {

        Ambulance ambulance = ambulanceService.registerAmbulance(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ambulance registered successfully", ambulance));
    }

    /**
     * GET /api/v1/ambulances
     * Get all available ambulances
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Ambulance>>> getAvailableAmbulances() {
        List<Ambulance> ambulances = ambulanceService.getAvailableAmbulances();
        return ResponseEntity.ok(ApiResponse.success("Available ambulances", ambulances));
    }

    /**
     * GET /api/v1/ambulances/{id}
     * Get ambulance by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Ambulance>> getById(@PathVariable Long id) {
        Ambulance ambulance = ambulanceService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Ambulance details", ambulance));
    }

    /**
     * GET /api/v1/ambulances/hospital/{hospitalId}
     * Get all ambulances of a hospital
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<ApiResponse<List<Ambulance>>> getByHospital(
            @PathVariable Long hospitalId) {

        List<Ambulance> ambulances = ambulanceService.getByHospital(hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Hospital ambulances", ambulances));
    }

    /**
     * GET /api/v1/ambulances/nearest?lat=13.08&lng=80.27&radius=10
     * Find nearest available ambulance to a location
     */
    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> findNearest(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10.0") double radius) {

        List<Ambulance> ambulances = ambulanceService.findNearestAmbulances(lat, lng, radius);

        // Add distance and maps URL to each ambulance
        List<Map<String, Object>> result = ambulances.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("vehicleNumber", a.getVehicleNumber());
            map.put("driverName", a.getDriverName());
            map.put("driverPhone", a.getDriverPhone());
            map.put("status", a.getStatus());
            map.put("currentLatitude", a.getCurrentLatitude());
            map.put("currentLongitude", a.getCurrentLongitude());
            map.put("locationUpdatedAt", a.getLocationUpdatedAt());

            if (a.getCurrentLatitude() != null) {
                double distance = locationUtil.calculateDistance(
                        lat, lng, a.getCurrentLatitude(), a.getCurrentLongitude());
                map.put("distanceKm", Math.round(distance * 10.0) / 10.0);
                map.put("googleMapsUrl", locationUtil.generateGoogleMapsUrl(
                        a.getCurrentLatitude(), a.getCurrentLongitude()));
                map.put("directionsUrl", locationUtil.generateDirectionsUrl(
                        lat, lng, a.getCurrentLatitude(), a.getCurrentLongitude()));
            }
            return map;
        }).toList();

        return ResponseEntity.ok(ApiResponse.success("Nearest ambulances", result));
    }

    /**
     * PUT /api/v1/ambulances/{id}/location
     * Update ambulance live location (called by driver app every 10 seconds)
     */
    @PutMapping("/{id}/location")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<Ambulance>> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationUpdateRequest request) {

        Ambulance ambulance = ambulanceService.updateLocation(id, request);
        return ResponseEntity.ok(ApiResponse.success("Location updated", ambulance));
    }

    /**
     * PUT /api/v1/ambulances/{id}/status?status=ON_DUTY
     * Update ambulance status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<Ambulance>> updateStatus(
            @PathVariable Long id,
            @RequestParam AmbulanceStatus status) {

        Ambulance ambulance = ambulanceService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated to " + status, ambulance));
    }

    /**
     * POST /api/v1/ambulances/{id}/dispatch/{requestId}
     * Dispatch ambulance to a blood request location
     */
    @PostMapping("/{id}/dispatch/{requestId}")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<Ambulance>> dispatch(
            @PathVariable Long id,
            @PathVariable Long requestId) {

        Ambulance ambulance = ambulanceService.dispatchToRequest(id, requestId);
        return ResponseEntity.ok(ApiResponse.success(
                "🚑 Ambulance dispatched to blood request #" + requestId, ambulance));
    }
}
