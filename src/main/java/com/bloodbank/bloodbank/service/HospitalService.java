package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.HospitalRequest;
import com.bloodbank.bloodbank.entity.Hospital;

import java.util.List;

public interface HospitalService {

    Hospital registerHospital(Long userId, HospitalRequest request);

    Hospital updateHospital(Long hospitalId, HospitalRequest request);

    Hospital getById(Long hospitalId);

    Hospital getByUserId(Long userId);

    List<Hospital> getAllHospitals();

    List<Hospital> searchByCity(String city);

    List<Hospital> findNearbyHospitals(double latitude, double longitude, double radiusKm);
}
