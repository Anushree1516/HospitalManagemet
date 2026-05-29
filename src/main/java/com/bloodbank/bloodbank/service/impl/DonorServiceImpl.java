package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.DonorRequest;
import com.bloodbank.bloodbank.entity.Donor;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.enums.DonorStatus;
import com.bloodbank.bloodbank.exception.ResourceAlreadyExistsException;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.DonorRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.DonorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonorServiceImpl implements DonorService {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Donor registerDonor(Long userId, DonorRequest request) {
        if (donorRepository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException("User already registered as donor");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Donor donor = Donor.builder()
                .user(user)
                .bloodGroup(request.getBloodGroup())
                .age(request.getAge())
                .weightKg(request.getWeightKg())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .city(request.getCity())
                .isNotifyEnabled(request.getIsNotifyEnabled())
                .status(DonorStatus.AVAILABLE)
                .totalDonations(0)
                .build();

        return donorRepository.save(donor);
    }

    @Override
    @Transactional
    public Donor updateDonorStatus(Long userId, DonorStatus status) {
        Donor donor = getDonorByUserId(userId);
        donor.setStatus(status);
        return donorRepository.save(donor);
    }

    @Override
    public Donor getDonorByUserId(Long userId) {
        return donorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found"));
    }

    @Override
    public List<Donor> getDonorsByBloodGroup(BloodGroup bloodGroup) {
        return donorRepository.findByBloodGroupAndStatus(bloodGroup, DonorStatus.AVAILABLE);
    }

    @Override
    public List<Donor> getNearbyDonors(BloodGroup bloodGroup, double lat, double lng, double radiusKm) {
        return donorRepository.findNearbyAvailableDonors(bloodGroup.name(), lat, lng, radiusKm);
    }
}
