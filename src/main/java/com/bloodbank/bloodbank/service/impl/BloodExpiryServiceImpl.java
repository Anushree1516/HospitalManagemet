package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.BloodExpiryRequest;
import com.bloodbank.bloodbank.entity.BloodExpiry;
import com.bloodbank.bloodbank.entity.Hospital;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.BloodExpiryRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.service.BloodExpiryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodExpiryServiceImpl implements BloodExpiryService {

    private static final Logger logger = LoggerFactory.getLogger(BloodExpiryServiceImpl.class);

    private final BloodExpiryRepository bloodExpiryRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional
    public BloodExpiry addBloodUnit(Long hospitalId, BloodExpiryRequest request) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        BloodExpiry expiry = BloodExpiry.builder()
                .hospital(hospital)
                .bloodGroup(request.getBloodGroup())
                .units(request.getUnits())
                .collectionDate(request.getCollectionDate())
                .expiryDate(request.getExpiryDate())
                .bagNumber(request.getBagNumber())
                .donorName(request.getDonorName())
                .isExpired(false)
                .isDiscarded(false)
                .build();

        return bloodExpiryRepository.save(expiry);
    }

    @Override
    public List<BloodExpiry> getExpiringBlood(Long hospitalId, int withinDays) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(withinDays);
        return bloodExpiryRepository.findExpiringBlood(hospitalId, today, futureDate);
    }

    @Override
    public List<BloodExpiry> getExpiredBlood(Long hospitalId) {
        return bloodExpiryRepository.findExpiredBlood(hospitalId, LocalDate.now());
    }

    @Override
    public List<BloodExpiry> getAllExpiredBlood() {
        return bloodExpiryRepository.findAllExpiredBlood(LocalDate.now());
    }

    @Override
    @Transactional
    public BloodExpiry discardExpiredUnit(Long expiryId) {
        BloodExpiry expiry = bloodExpiryRepository.findById(expiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood unit not found"));
        expiry.setIsDiscarded(true);
        expiry.setIsExpired(true);
        expiry.setDiscardedAt(LocalDateTime.now());
        return bloodExpiryRepository.save(expiry);
    }

    @Override
    public List<BloodExpiry> getHospitalBloodUnits(Long hospitalId) {
        return bloodExpiryRepository.findByHospitalId(hospitalId);
    }

    // ── Auto scheduler runs every day at 8 AM ──────────────
    // Marks all expired blood automatically
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void autoMarkExpiredBlood() {
        List<BloodExpiry> expired = bloodExpiryRepository.findAllExpiredBlood(LocalDate.now());
        for (BloodExpiry unit : expired) {
            if (!unit.getIsExpired()) {
                unit.setIsExpired(true);
                bloodExpiryRepository.save(unit);
                logger.warn("🩸 Blood unit expired! Hospital: {} | Group: {} | Bag: {}",
                        unit.getHospital().getHospitalName(),
                        unit.getBloodGroup().getLabel(),
                        unit.getBagNumber());
            }
        }
        if (!expired.isEmpty()) {
            logger.info("⚠️ Auto-marked {} blood units as expired", expired.size());
        }
    }
}
