package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.AmbulanceRequest;
import com.bloodbank.bloodbank.dto.request.LocationUpdateRequest;
import com.bloodbank.bloodbank.entity.Ambulance;
import com.bloodbank.bloodbank.enums.AmbulanceStatus;

import java.util.List;

public interface AmbulanceService {

    Ambulance registerAmbulance(AmbulanceRequest request);

    Ambulance updateLocation(Long ambulanceId, LocationUpdateRequest request);

    Ambulance updateStatus(Long ambulanceId, AmbulanceStatus status);

    Ambulance dispatchToRequest(Long ambulanceId, Long bloodRequestId);

    List<Ambulance> findNearestAmbulances(double latitude, double longitude, double radiusKm);

    List<Ambulance> getByHospital(Long hospitalId);

    List<Ambulance> getAvailableAmbulances();

    Ambulance getById(Long id);
}
