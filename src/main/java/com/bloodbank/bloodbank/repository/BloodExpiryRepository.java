package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.BloodExpiry;
import com.bloodbank.bloodbank.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodExpiryRepository extends JpaRepository<BloodExpiry, Long> {

    List<BloodExpiry> findByHospitalId(Long hospitalId);

    List<BloodExpiry> findByHospitalIdAndBloodGroup(Long hospitalId, BloodGroup bloodGroup);

    // Find blood expiring within N days
    @Query("""
            SELECT be FROM BloodExpiry be
            WHERE be.hospital.id = :hospitalId
            AND be.isExpired = false
            AND be.isDiscarded = false
            AND be.expiryDate BETWEEN :today AND :futureDate
            ORDER BY be.expiryDate ASC
            """)
    List<BloodExpiry> findExpiringBlood(
            @Param("hospitalId") Long hospitalId,
            @Param("today") LocalDate today,
            @Param("futureDate") LocalDate futureDate
    );

    // Find already expired blood
    @Query("""
            SELECT be FROM BloodExpiry be
            WHERE be.hospital.id = :hospitalId
            AND be.expiryDate < :today
            AND be.isDiscarded = false
            ORDER BY be.expiryDate ASC
            """)
    List<BloodExpiry> findExpiredBlood(
            @Param("hospitalId") Long hospitalId,
            @Param("today") LocalDate today
    );

    // All expired blood across system (for admin)
    @Query("SELECT be FROM BloodExpiry be WHERE be.expiryDate < :today AND be.isDiscarded = false")
    List<BloodExpiry> findAllExpiredBlood(@Param("today") LocalDate today);
}
