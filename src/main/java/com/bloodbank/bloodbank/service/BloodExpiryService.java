package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.BloodExpiryRequest;
import com.bloodbank.bloodbank.entity.BloodExpiry;

import java.util.List;

public interface BloodExpiryService {

    BloodExpiry addBloodUnit(Long hospitalId, BloodExpiryRequest request);

    List<BloodExpiry> getExpiringBlood(Long hospitalId, int withinDays);

    List<BloodExpiry> getExpiredBlood(Long hospitalId);

    List<BloodExpiry> getAllExpiredBlood();

    BloodExpiry discardExpiredUnit(Long expiryId);

    List<BloodExpiry> getHospitalBloodUnits(Long hospitalId);
}
