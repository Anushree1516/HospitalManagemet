package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByEmail(String email);

    Optional<Hospital> findByUserId(Long userId);

    List<Hospital> findByCity(String city);

    List<Hospital> findByCityAndIsActive(String city, Boolean isActive);

    Boolean existsByEmail(String email);

    Boolean existsByRegistrationNumber(String registrationNumber);

    // Find hospitals within radius using Haversine formula
    @Query(value = """
            SELECT * FROM hospitals h
            WHERE h.latitude IS NOT NULL AND h.longitude IS NOT NULL AND h.is_active = true
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(h.latitude))
            * cos(radians(h.longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(h.latitude)))) < :radiusKm
            ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(h.latitude))
            * cos(radians(h.longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(h.latitude)))) ASC
            """, nativeQuery = true)
    List<Hospital> findHospitalsWithinRadius(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusKm") double radiusKm
    );
}
