package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.BloodExpiryRequest;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.BloodExpiry;
import com.bloodbank.bloodbank.service.BloodExpiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blood-expiry")
@RequiredArgsConstructor
public class BloodExpiryController {

    private final BloodExpiryService bloodExpiryService;

    /**
     * POST /api/v1/blood-expiry/hospital/{hospitalId}
     * Add blood unit with expiry tracking
     */
    @PostMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<BloodExpiry>> addBloodUnit(
            @PathVariable Long hospitalId,
            @Valid @RequestBody BloodExpiryRequest request) {

        BloodExpiry expiry = bloodExpiryService.addBloodUnit(hospitalId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Blood unit added with expiry tracking", expiry));
    }

    /**
     * GET /api/v1/blood-expiry/hospital/{hospitalId}
     * Get all blood units of a hospital
     */
    @GetMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<List<BloodExpiry>>> getHospitalUnits(
            @PathVariable Long hospitalId) {

        List<BloodExpiry> units = bloodExpiryService.getHospitalBloodUnits(hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Blood units", units));
    }

    /**
     * GET /api/v1/blood-expiry/hospital/{hospitalId}/expiring?days=7
     * Get blood units expiring within N days (default 7)
     */
    @GetMapping("/hospital/{hospitalId}/expiring")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<List<BloodExpiry>>> getExpiringBlood(
            @PathVariable Long hospitalId,
            @RequestParam(defaultValue = "7") int days) {

        List<BloodExpiry> expiring = bloodExpiryService.getExpiringBlood(hospitalId, days);
        return ResponseEntity.ok(
                ApiResponse.success("Blood expiring within " + days + " days", expiring));
    }

    /**
     * GET /api/v1/blood-expiry/hospital/{hospitalId}/expired
     * Get all expired blood units of a hospital
     */
    @GetMapping("/hospital/{hospitalId}/expired")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<List<BloodExpiry>>> getExpiredBlood(
            @PathVariable Long hospitalId) {

        List<BloodExpiry> expired = bloodExpiryService.getExpiredBlood(hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Expired blood units", expired));
    }

    /**
     * GET /api/v1/blood-expiry/all-expired
     * Get all expired blood across all hospitals (Admin only)
     */
    @GetMapping("/all-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BloodExpiry>>> getAllExpired() {
        List<BloodExpiry> expired = bloodExpiryService.getAllExpiredBlood();
        return ResponseEntity.ok(ApiResponse.success("All expired blood units", expired));
    }

    /**
     * PUT /api/v1/blood-expiry/{id}/discard
     * Mark expired blood unit as discarded
     */
    @PutMapping("/{id}/discard")
    @PreAuthorize("hasAnyRole('HOSPITAL','ADMIN')")
    public ResponseEntity<ApiResponse<BloodExpiry>> discardUnit(@PathVariable Long id) {
        BloodExpiry expiry = bloodExpiryService.discardExpiredUnit(id);
        return ResponseEntity.ok(ApiResponse.success("Blood unit marked as discarded", expiry));
    }
}
