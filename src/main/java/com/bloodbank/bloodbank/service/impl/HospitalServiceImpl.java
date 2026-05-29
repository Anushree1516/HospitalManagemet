package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.HospitalRequest;
import com.bloodbank.bloodbank.entity.Hospital;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.exception.ResourceAlreadyExistsException;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Hospital registerHospital(Long userId, HospitalRequest request) {
        if (hospitalRepository.findByUserId(userId).isPresent()) {
            throw new ResourceAlreadyExistsException("Hospital already registered for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Hospital hospital = Hospital.builder()
                .hospitalName(request.getHospitalName())
                .registrationNumber(request.getRegistrationNumber())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .isActive(true)
                .build();

        return hospitalRepository.save(hospital);
    }

    @Override
    @Transactional
    public Hospital updateHospital(Long hospitalId, HospitalRequest request) {
        Hospital hospital = getById(hospitalId);
        hospital.setHospitalName(request.getHospitalName());
        hospital.setPhoneNumber(request.getPhoneNumber());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setState(request.getState());
        hospital.setPincode(request.getPincode());
        if (request.getLatitude() != null) hospital.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) hospital.setLongitude(request.getLongitude());
        return hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getById(Long hospitalId) {
        return hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + hospitalId));
    }

    @Override
    public Hospital getByUserId(Long userId) {
        return hospitalRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No hospital profile for user"));
    }

    @Override
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    @Override
    public List<Hospital> searchByCity(String city) {
        return hospitalRepository.findByCityAndIsActive(city, true);
    }

    @Override
    public List<Hospital> findNearbyHospitals(double latitude, double longitude, double radiusKm) {
        return hospitalRepository.findHospitalsWithinRadius(latitude, longitude, radiusKm);
    }
}
