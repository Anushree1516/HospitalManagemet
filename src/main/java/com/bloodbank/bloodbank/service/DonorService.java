package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.DonorRequest;
import com.bloodbank.bloodbank.entity.Donor;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.enums.DonorStatus;

import java.util.List;

public interface DonorService {

    Donor registerDonor(Long userId, DonorRequest request);

    Donor updateDonorStatus(Long userId, DonorStatus status);

    Donor getDonorByUserId(Long userId);

    List<Donor> getDonorsByBloodGroup(BloodGroup bloodGroup);

    List<Donor> getNearbyDonors(BloodGroup bloodGroup, double lat, double lng, double radiusKm);
}
