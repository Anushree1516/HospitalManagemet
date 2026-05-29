package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.BloodStockRequest;
import com.bloodbank.bloodbank.dto.response.BloodSearchResponse;
import com.bloodbank.bloodbank.entity.BloodStock;
import com.bloodbank.bloodbank.enums.BloodGroup;

import java.util.List;
import java.util.Map;

public interface BloodStockService {

    BloodStock addOrUpdateStock(Long hospitalId, BloodStockRequest request);

    BloodStock updateStockQuantity(Long hospitalId, BloodGroup bloodGroup, Integer quantity);

    void deleteStock(Long hospitalId, BloodGroup bloodGroup);

    List<BloodStock> getStockByHospital(Long hospitalId);

    // Search by group + city
    List<BloodSearchResponse> searchBloodByGroupAndCity(BloodGroup bloodGroup, String city);

    // Search by group + user location (Google Maps Integration)
    List<BloodSearchResponse> searchNearbyBlood(BloodGroup bloodGroup,
                                                 double latitude,
                                                 double longitude,
                                                 double radiusKm);

    // Dashboard: total blood per group across all hospitals
    Map<String, Integer> getTotalBloodInventory();
}
