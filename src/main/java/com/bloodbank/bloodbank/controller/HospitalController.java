package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.HospitalRequest;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.Hospital;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;
    private final UserRepository userRepository;

    /**
     * POST /api/v1/hospitals/register
     * Hospital registers its profile
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<ApiResponse<Hospital>> registerHospital(
            @Valid @RequestBody HospitalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Hospital hospital = hospitalService.registerHospital(user.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hospital registered successfully", hospital));
    }

    /**
     * GET /api/v1/hospitals
     * Get all hospitals (public)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Hospital>>> getAllHospitals() {
        List<Hospital> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(ApiResponse.success("All hospitals", hospitals));
    }

    /**
     * GET /api/v1/hospitals/{id}
     * Get hospital by ID (public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Hospital>> getHospitalById(@PathVariable Long id) {
        Hospital hospital = hospitalService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Hospital details", hospital));
    }

    /**
     * GET /api/v1/hospitals/search?city=Chennai
     * Search hospitals by city (public)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Hospital>>> searchByCity(@RequestParam String city) {
        List<Hospital> hospitals = hospitalService.searchByCity(city);
        return ResponseEntity.ok(ApiResponse.success("Hospitals in " + city, hospitals));
    }

    /**
     * GET /api/v1/hospitals/nearby?lat=13.08&lng=80.27&radius=10
     * Find hospitals near a location (Google Maps Integration)
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<Hospital>>> findNearbyHospitals(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10.0") double radius) {

        List<Hospital> hospitals = hospitalService.findNearbyHospitals(lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.success("Nearby hospitals", hospitals));
    }

    /**
     * PUT /api/v1/hospitals/{id}
     * Update hospital profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<ApiResponse<Hospital>> updateHospital(
            @PathVariable Long id,
            @Valid @RequestBody HospitalRequest request) {

        Hospital updated = hospitalService.updateHospital(id, request);
        return ResponseEntity.ok(ApiResponse.success("Hospital updated successfully", updated));
    }

    /**
     * GET /api/v1/hospitals/my
     * Get logged-in hospital's own profile
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<ApiResponse<Hospital>> getMyHospital(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Hospital hospital = hospitalService.getByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Your hospital profile", hospital));
    }
}
