package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.Donor;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.enums.DonorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByUserId(Long userId);

    List<Donor> findByBloodGroup(BloodGroup bloodGroup);

    List<Donor> findByBloodGroupAndStatus(BloodGroup bloodGroup, DonorStatus status);

    // Find available donors nearby for emergency notifications
    @Query(value = """
            SELECT d.* FROM donors d
            JOIN users u ON d.user_id = u.id
            WHERE d.blood_group = :bloodGroup
            AND d.status = 'AVAILABLE'
            AND d.is_notify_enabled = true
            AND u.latitude IS NOT NULL AND u.longitude IS NOT NULL
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude))
            * cos(radians(u.longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(u.latitude)))) < :radiusKm
            """, nativeQuery = true)
    List<Donor> findNearbyAvailableDonors(
            @Param("bloodGroup") String bloodGroup,
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusKm") double radiusKm
    );

    Boolean existsByUserId(Long userId);
}
