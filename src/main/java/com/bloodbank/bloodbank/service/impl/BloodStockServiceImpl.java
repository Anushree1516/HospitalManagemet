package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.BloodStockRequest;
import com.bloodbank.bloodbank.dto.response.BloodSearchResponse;
import com.bloodbank.bloodbank.entity.BloodStock;
import com.bloodbank.bloodbank.entity.Hospital;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.BloodStockRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.service.BloodStockService;
import com.bloodbank.bloodbank.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodStockServiceImpl implements BloodStockService {

    private final BloodStockRepository bloodStockRepository;
    private final HospitalRepository hospitalRepository;
    private final LocationUtil locationUtil;

    @Override
    @Transactional
    public BloodStock addOrUpdateStock(Long hospitalId, BloodStockRequest request) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + hospitalId));

        Optional<BloodStock> existing = bloodStockRepository
                .findByHospitalIdAndBloodGroup(hospitalId, request.getBloodGroup());

        if (existing.isPresent()) {
            BloodStock stock = existing.get();
            stock.setQuantity(request.getQuantity());
            stock.setUnitsAvailable(request.getQuantity());
            stock.setLastUpdated(LocalDateTime.now());
            return bloodStockRepository.save(stock);
        } else {
            BloodStock newStock = BloodStock.builder()
                    .hospital(hospital)
                    .bloodGroup(request.getBloodGroup())
                    .quantity(request.getQuantity())
                    .unitsAvailable(request.getQuantity())
                    .lastUpdated(LocalDateTime.now())
                    .build();
            return bloodStockRepository.save(newStock);
        }
    }

    @Override
    @Transactional
    public BloodStock updateStockQuantity(Long hospitalId, BloodGroup bloodGroup, Integer quantity) {
        BloodStock stock = bloodStockRepository
                .findByHospitalIdAndBloodGroup(hospitalId, bloodGroup)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for: " + bloodGroup.getLabel()));

        stock.setQuantity(quantity);
        stock.setUnitsAvailable(quantity);
        stock.setLastUpdated(LocalDateTime.now());
        return bloodStockRepository.save(stock);
    }

    @Override
    @Transactional
    public void deleteStock(Long hospitalId, BloodGroup bloodGroup) {
        BloodStock stock = bloodStockRepository
                .findByHospitalIdAndBloodGroup(hospitalId, bloodGroup)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
        bloodStockRepository.delete(stock);
    }

    @Override
    public List<BloodStock> getStockByHospital(Long hospitalId) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found: " + hospitalId);
        }
        return bloodStockRepository.findByHospitalId(hospitalId);
    }

    @Override
    public List<BloodSearchResponse> searchBloodByGroupAndCity(BloodGroup bloodGroup, String city) {
        List<BloodStock> stocks = bloodStockRepository
                .findAvailableBloodByGroupAndCity(bloodGroup, city);

        return stocks.stream()
                .map(this::mapToSearchResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloodSearchResponse> searchNearbyBlood(BloodGroup bloodGroup,
                                                         double latitude,
                                                         double longitude,
                                                         double radiusKm) {
        List<BloodStock> stocks = bloodStockRepository.findNearbyAvailableBlood(
                bloodGroup.name(), latitude, longitude, radiusKm
        );

        return stocks.stream()
                .map(stock -> {
                    BloodSearchResponse response = mapToSearchResponse(stock);
                    // Calculate exact distance
                    if (stock.getHospital().getLatitude() != null) {
                        double dist = locationUtil.calculateDistance(
                                latitude, longitude,
                                stock.getHospital().getLatitude(),
                                stock.getHospital().getLongitude()
                        );
                        response.setDistanceKm(Math.round(dist * 10.0) / 10.0);
                        // Add Google Maps directions URL
                        response.setGoogleMapsUrl(locationUtil.generateDirectionsUrl(
                                latitude, longitude,
                                stock.getHospital().getLatitude(),
                                stock.getHospital().getLongitude()
                        ));
                    }
                    return response;
                })
                .sorted((a, b) -> {
                    if (a.getDistanceKm() == null) return 1;
                    if (b.getDistanceKm() == null) return -1;
                    return Double.compare(a.getDistanceKm(), b.getDistanceKm());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getTotalBloodInventory() {
        List<Object[]> results = bloodStockRepository.getTotalBloodByGroup();
        Map<String, Integer> inventory = new HashMap<>();
        for (Object[] row : results) {
            BloodGroup group = (BloodGroup) row[0];
            Long total = (Long) row[1];
            inventory.put(group.getLabel(), total.intValue());
        }
        return inventory;
    }

    private BloodSearchResponse mapToSearchResponse(BloodStock stock) {
        Hospital hospital = stock.getHospital();
        return BloodSearchResponse.builder()
                .hospitalId(hospital.getId())
                .hospitalName(hospital.getHospitalName())
                .address(hospital.getAddress())
                .city(hospital.getCity())
                .state(hospital.getState())
                .phoneNumber(hospital.getPhoneNumber())
                .bloodGroup(stock.getBloodGroup())
                .availableUnits(stock.getQuantity())
                .latitude(hospital.getLatitude())
                .longitude(hospital.getLongitude())
                .googleMapsUrl(hospital.getLatitude() != null
                        ? locationUtil.generateGoogleMapsUrl(
                        hospital.getLatitude(), hospital.getLongitude())
                        : null)
                .build();
    }
}
