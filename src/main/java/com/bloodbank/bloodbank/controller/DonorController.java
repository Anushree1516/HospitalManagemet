package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.DonorRequest;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.Donor;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.enums.DonorStatus;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.DonorService;
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
@RequestMapping("/api/v1/donors")
@RequiredArgsConstructor
public class DonorController {

    private final DonorService donorService;
    private final UserRepository userRepository;

    /**
     * POST /api/v1/donors/register
     * Register current user as a blood donor
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('DONOR', 'USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Donor>> registerDonor(
            @Valid @RequestBody DonorRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Donor donor = donorService.registerDonor(user.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Donor registered successfully", donor));
    }

    /**
     * GET /api/v1/donors/my
     * Get my donor profile
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Donor>> getMyDonorProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Donor donor = donorService.getDonorByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Donor profile", donor));
    }

    /**
     * PUT /api/v1/donors/status?status=AVAILABLE
     * Update donor availability status
     */
    @PutMapping("/status")
    public ResponseEntity<ApiResponse<Donor>> updateStatus(
            @RequestParam DonorStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Donor donor = donorService.updateDonorStatus(user.getId(), status);
        return ResponseEntity.ok(ApiResponse.success("Donor status updated", donor));
    }

    /**
     * GET /api/v1/donors/search?group=A_POSITIVE
     * Find available donors by blood group
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Donor>>> searchByBloodGroup(
            @RequestParam BloodGroup group) {

        List<Donor> donors = donorService.getDonorsByBloodGroup(group);
        return ResponseEntity.ok(ApiResponse.success("Available donors", donors));
    }

    /**
     * GET /api/v1/donors/nearby?group=O_POSITIVE&lat=13.08&lng=80.27&radius=10
     * Find nearby available donors (for emergency use)
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<Donor>>> getNearbyDonors(
            @RequestParam BloodGroup group,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10.0") double radius) {

        List<Donor> donors = donorService.getNearbyDonors(group, lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.success("Nearby donors", donors));
    }
}
