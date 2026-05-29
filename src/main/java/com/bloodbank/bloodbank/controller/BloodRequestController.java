package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.BloodRequestDto;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.BloodRequest;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.enums.RequestStatus;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.BloodRequestService;
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
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;
    private final UserRepository userRepository;

    /**
     * POST /api/v1/requests
     * Create a new blood request (any authenticated user)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BloodRequest>> createRequest(
            @Valid @RequestBody BloodRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        BloodRequest request = bloodRequestService.createRequest(user.getId(), dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Blood request created successfully", request));
    }

    /**
     * GET /api/v1/requests/my
     * Get my own blood requests
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<BloodRequest> requests = bloodRequestService.getRequestsByUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Your blood requests", requests));
    }

    /**
     * GET /api/v1/requests/emergency
     * Get all active emergency requests (authenticated)
     */
    @GetMapping("/emergency")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> getEmergencyRequests() {
        List<BloodRequest> requests = bloodRequestService.getEmergencyRequests();
        return ResponseEntity.ok(ApiResponse.success("Emergency requests", requests));
    }

    /**
     * GET /api/v1/requests/hospital/{hospitalId}
     * Get requests assigned to a hospital
     */
    @GetMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> getHospitalRequests(
            @PathVariable Long hospitalId) {

        List<BloodRequest> requests = bloodRequestService.getRequestsByHospital(hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Hospital blood requests", requests));
    }

    /**
     * GET /api/v1/requests/{id}
     * Get a single blood request by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BloodRequest>> getById(@PathVariable Long id) {
        BloodRequest request = bloodRequestService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Blood request details", request));
    }

    /**
     * PUT /api/v1/requests/{id}/status?status=APPROVED
     * Update request status (Hospital or Admin only)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<ApiResponse<BloodRequest>> updateStatus(
            @PathVariable Long id,
            @RequestParam RequestStatus status) {

        BloodRequest updated = bloodRequestService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Request status updated", updated));
    }

    /**
     * GET /api/v1/requests (ADMIN only)
     * Get all requests
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> getAllRequests() {
        List<BloodRequest> requests = bloodRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success("All blood requests", requests));
    }
}
