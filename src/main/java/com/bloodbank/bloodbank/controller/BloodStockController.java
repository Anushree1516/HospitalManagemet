package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.BloodStockRequest;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.dto.response.BloodSearchResponse;
import com.bloodbank.bloodbank.entity.BloodStock;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.service.BloodStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BloodStockController {

    private final BloodStockService bloodStockService;

    // ============================================================
    // PUBLIC ENDPOINTS (no auth required)
    // ============================================================

    /**
     * GET /api/v1/blood/search?group=A_POSITIVE&city=Chennai
     * Search blood by group and city
     */
    @GetMapping("/blood/search")
    public ResponseEntity<ApiResponse<List<BloodSearchResponse>>> searchByCity(
            @RequestParam BloodGroup group,
            @RequestParam String city) {

        List<BloodSearchResponse> results = bloodStockService.searchBloodByGroupAndCity(group, city);
        return ResponseEntity.ok(ApiResponse.success("Blood search results", results));
    }

    /**
     * GET /api/v1/blood/nearby?group=O_POSITIVE&lat=13.08&lng=80.27&radius=15
     * Search blood near a location (Google Maps integration)
     */
    @GetMapping("/blood/nearby")
    public ResponseEntity<ApiResponse<List<BloodSearchResponse>>> searchNearby(
            @RequestParam BloodGroup group,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10.0") double radius) {

        List<BloodSearchResponse> results = bloodStockService.searchNearbyBlood(group, lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.success("Nearby blood results", results));
    }

    /**
     * GET /api/v1/blood/inventory
     * Total blood inventory across all hospitals (public dashboard)
     */
    @GetMapping("/blood/inventory")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getTotalInventory() {
        Map<String, Integer> inventory = bloodStockService.getTotalBloodInventory();
        return ResponseEntity.ok(ApiResponse.success("Blood inventory", inventory));
    }

    // ============================================================
    // HOSPITAL STOCK MANAGEMENT (HOSPITAL + ADMIN role)
    // ============================================================

    /**
     * POST /api/v1/hospitals/{hospitalId}/stock
     * Add or update blood stock
     */
    @PostMapping("/hospitals/{hospitalId}/stock")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<ApiResponse<BloodStock>> addOrUpdateStock(
            @PathVariable Long hospitalId,
            @Valid @RequestBody BloodStockRequest request) {

        BloodStock stock = bloodStockService.addOrUpdateStock(hospitalId, request);
        return ResponseEntity.ok(ApiResponse.success("Blood stock updated", stock));
    }

    /**
     * GET /api/v1/hospitals/{hospitalId}/stock
     * Get all blood stocks of a hospital
     */
    @GetMapping("/hospitals/{hospitalId}/stock")
    public ResponseEntity<ApiResponse<List<BloodStock>>> getHospitalStock(
            @PathVariable Long hospitalId) {

        List<BloodStock> stocks = bloodStockService.getStockByHospital(hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Hospital blood stock", stocks));
    }

    /**
     * PUT /api/v1/hospitals/{hospitalId}/stock/{bloodGroup}
     * Update quantity of a specific blood group
     */
    @PutMapping("/hospitals/{hospitalId}/stock/{bloodGroup}")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<ApiResponse<BloodStock>> updateQuantity(
            @PathVariable Long hospitalId,
            @PathVariable BloodGroup bloodGroup,
            @RequestParam Integer quantity) {

        BloodStock stock = bloodStockService.updateStockQuantity(hospitalId, bloodGroup, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock quantity updated", stock));
    }

    /**
     * DELETE /api/v1/hospitals/{hospitalId}/stock/{bloodGroup}
     * Delete expired/removed blood stock
     */
    @DeleteMapping("/hospitals/{hospitalId}/stock/{bloodGroup}")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStock(
            @PathVariable Long hospitalId,
            @PathVariable BloodGroup bloodGroup) {

        bloodStockService.deleteStock(hospitalId, bloodGroup);
        return ResponseEntity.ok(ApiResponse.success("Blood stock deleted"));
    }
}
