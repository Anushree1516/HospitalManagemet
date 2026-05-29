package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.BloodRequest;
import com.bloodbank.bloodbank.entity.Hospital;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.enums.RequestStatus;
import com.bloodbank.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.BloodStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final BloodStockService bloodStockService;

    /**
     * GET /api/v1/admin/dashboard
     * Full system dashboard stats
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        Map<String, Object> stats = new HashMap<>();

        // User counts
        stats.put("totalUsers", userRepository.count());

        // Hospital counts
        stats.put("totalHospitals", hospitalRepository.count());

        // Request stats
        stats.put("totalRequests", bloodRequestRepository.count());
        stats.put("pendingRequests", bloodRequestRepository.countByStatus(RequestStatus.PENDING));
        stats.put("fulfilledRequests", bloodRequestRepository.countByStatus(RequestStatus.FULFILLED));
        stats.put("emergencyRequests", bloodRequestRepository.countByIsEmergencyTrue());

        // Blood inventory
        stats.put("bloodInventory", bloodStockService.getTotalBloodInventory());

        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", stats));
    }

    /**
     * GET /api/v1/admin/users
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("All users", users));
    }

    /**
     * PUT /api/v1/admin/users/{id}/toggle-active
     * Activate or deactivate a user account
     */
    @PutMapping("/users/{id}/toggle-active")
    public ResponseEntity<ApiResponse<User>> toggleUserActive(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(!user.getIsActive());
        User updated = userRepository.save(user);
        String status = updated.getIsActive() ? "activated" : "deactivated";
        return ResponseEntity.ok(ApiResponse.success("User " + status, updated));
    }

    /**
     * GET /api/v1/admin/hospitals
     * Get all hospitals
     */
    @GetMapping("/hospitals")
    public ResponseEntity<ApiResponse<List<Hospital>>> getAllHospitals() {
        List<Hospital> hospitals = hospitalRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("All hospitals", hospitals));
    }

    /**
     * GET /api/v1/admin/requests
     * Get all blood requests with filter
     */
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> getAllRequests(
            @RequestParam(required = false) RequestStatus status) {

        List<BloodRequest> requests = (status != null)
                ? bloodRequestRepository.findByStatus(status)
                : bloodRequestRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Blood requests", requests));
    }
}
