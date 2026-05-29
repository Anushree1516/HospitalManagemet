package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.AmbulanceRequest;
import com.bloodbank.bloodbank.dto.request.LocationUpdateRequest;
import com.bloodbank.bloodbank.entity.Ambulance;
import com.bloodbank.bloodbank.entity.BloodRequest;
import com.bloodbank.bloodbank.entity.Hospital;
import com.bloodbank.bloodbank.entity.LiveLocationTracking;
import com.bloodbank.bloodbank.enums.AmbulanceStatus;
import com.bloodbank.bloodbank.exception.ResourceAlreadyExistsException;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.AmbulanceRepository;
import com.bloodbank.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.repository.LiveLocationTrackingRepository;
import com.bloodbank.bloodbank.service.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmbulanceServiceImpl implements AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final LiveLocationTrackingRepository locationTrackingRepository;

    @Override
    @Transactional
    public Ambulance registerAmbulance(AmbulanceRequest request) {
        if (ambulanceRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new ResourceAlreadyExistsException(
                    "Ambulance already registered: " + request.getVehicleNumber());
        }

        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        }

        Ambulance ambulance = Ambulance.builder()
                .vehicleNumber(request.getVehicleNumber())
                .driverName(request.getDriverName())
                .driverPhone(request.getDriverPhone())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .hospital(hospital)
                .currentLatitude(request.getCurrentLatitude())
                .currentLongitude(request.getCurrentLongitude())
                .locationUpdatedAt(LocalDateTime.now())
                .status(AmbulanceStatus.AVAILABLE)
                .isActive(true)
                .build();

        return ambulanceRepository.save(ambulance);
    }

    @Override
    @Transactional
    public Ambulance updateLocation(Long ambulanceId, LocationUpdateRequest request) {
        Ambulance ambulance = getById(ambulanceId);

        // Update ambulance current location
        ambulance.setCurrentLatitude(request.getLatitude());
        ambulance.setCurrentLongitude(request.getLongitude());
        ambulance.setLocationUpdatedAt(LocalDateTime.now());
        ambulanceRepository.save(ambulance);

        // Save location history for tracking
        BloodRequest bloodRequest = null;
        if (request.getBloodRequestId() != null) {
            bloodRequest = bloodRequestRepository.findById(request.getBloodRequestId())
                    .orElse(null);
        }

        LiveLocationTracking tracking = LiveLocationTracking.builder()
                .ambulance(ambulance)
                .bloodRequest(bloodRequest)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .speedKmh(request.getSpeedKmh())
                .headingDegrees(request.getHeadingDegrees())
                .accuracyMeters(request.getAccuracyMeters())
                .address(request.getAddress())
                .trackingType("AMBULANCE")
                .build();

        locationTrackingRepository.save(tracking);
        return ambulance;
    }

    @Override
    @Transactional
    public Ambulance updateStatus(Long ambulanceId, AmbulanceStatus status) {
        Ambulance ambulance = getById(ambulanceId);
        ambulance.setStatus(status);

        // If returning to base → clear assigned request
        if (status == AmbulanceStatus.AVAILABLE || status == AmbulanceStatus.RETURNING) {
            ambulance.setAssignedRequest(null);
        }

        return ambulanceRepository.save(ambulance);
    }

    @Override
    @Transactional
    public Ambulance dispatchToRequest(Long ambulanceId, Long bloodRequestId) {
        Ambulance ambulance = getById(ambulanceId);

        if (ambulance.getStatus() != AmbulanceStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "Ambulance is not available. Current status: " + ambulance.getStatus());
        }

        BloodRequest bloodRequest = bloodRequestRepository.findById(bloodRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found"));

        ambulance.setStatus(AmbulanceStatus.ON_DUTY);
        ambulance.setAssignedRequest(bloodRequest);
        return ambulanceRepository.save(ambulance);
    }

    @Override
    public List<Ambulance> findNearestAmbulances(double latitude, double longitude, double radiusKm) {
        return ambulanceRepository.findNearestAvailableAmbulances(latitude, longitude, radiusKm);
    }

    @Override
    public List<Ambulance> getByHospital(Long hospitalId) {
        return ambulanceRepository.findByHospitalId(hospitalId);
    }

    @Override
    public List<Ambulance> getAvailableAmbulances() {
        return ambulanceRepository.findByStatus(AmbulanceStatus.AVAILABLE);
    }

    @Override
    public Ambulance getById(Long id) {
        return ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ambulance not found: " + id));
    }
}
