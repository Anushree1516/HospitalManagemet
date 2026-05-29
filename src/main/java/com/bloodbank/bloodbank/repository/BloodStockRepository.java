package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.BloodStock;
import com.bloodbank.bloodbank.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodStockRepository extends JpaRepository<BloodStock, Long> {

    List<BloodStock> findByHospitalId(Long hospitalId);

    Optional<BloodStock> findByHospitalIdAndBloodGroup(Long hospitalId, BloodGroup bloodGroup);

    // Find all hospitals having a specific blood group with quantity > 0
    List<BloodStock> findByBloodGroupAndQuantityGreaterThan(BloodGroup bloodGroup, Integer quantity);

    // Search blood by group and city
    @Query("""
            SELECT bs FROM BloodStock bs
            JOIN bs.hospital h
            WHERE bs.bloodGroup = :bloodGroup
            AND bs.quantity > 0
            AND h.city = :city
            AND h.isActive = true
            """)
    List<BloodStock> findAvailableBloodByGroupAndCity(
            @Param("bloodGroup") BloodGroup bloodGroup,
            @Param("city") String city
    );

    // Find available blood near a location
    @Query(value = """
            SELECT bs.* FROM blood_stocks bs
            JOIN hospitals h ON bs.hospital_id = h.id
            WHERE bs.blood_group = :bloodGroup
            AND bs.quantity > 0
            AND h.is_active = true
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(h.latitude))
            * cos(radians(h.longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(h.latitude)))) < :radiusKm
            ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(h.latitude))
            * cos(radians(h.longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(h.latitude)))) ASC
            """, nativeQuery = true)
    List<BloodStock> findNearbyAvailableBlood(
            @Param("bloodGroup") String bloodGroup,
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusKm") double radiusKm
    );

    // Get total blood units per group across all hospitals
    @Query("SELECT bs.bloodGroup, SUM(bs.quantity) FROM BloodStock bs GROUP BY bs.bloodGroup")
    List<Object[]> getTotalBloodByGroup();
}
