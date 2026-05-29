package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.Ambulance;
import com.bloodbank.bloodbank.enums.AmbulanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {

    List<Ambulance> findByHospitalId(Long hospitalId);

    List<Ambulance> findByStatus(AmbulanceStatus status);

    List<Ambulance> findByHospitalIdAndStatus(Long hospitalId, AmbulanceStatus status);

    // Find nearest available ambulance using Haversine formula
    @Query(value = """
            SELECT a.* FROM ambulances a
            WHERE a.status = 'AVAILABLE'
            AND a.is_active = true
            AND a.current_latitude IS NOT NULL
            AND a.current_longitude IS NOT NULL
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(a.current_latitude))
            * cos(radians(a.current_longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(a.current_latitude)))) < :radiusKm
            ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(a.current_latitude))
            * cos(radians(a.current_longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(a.current_latitude)))) ASC
            LIMIT 5
            """, nativeQuery = true)
    List<Ambulance> findNearestAvailableAmbulances(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusKm") double radiusKm
    );

    boolean existsByVehicleNumber(String vehicleNumber);
}
