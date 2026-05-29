package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.LiveLocationTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveLocationTrackingRepository extends JpaRepository<LiveLocationTracking, Long> {

    // Get latest location of ambulance
    Optional<LiveLocationTracking> findTopByAmbulanceIdOrderByRecordedAtDesc(Long ambulanceId);

    // Get latest location of user
    Optional<LiveLocationTracking> findTopByUserIdOrderByRecordedAtDesc(Long userId);

    // Get full location history for a blood request
    List<LiveLocationTracking> findByBloodRequestIdOrderByRecordedAtDesc(Long bloodRequestId);

    // Get location history for ambulance
    List<LiveLocationTracking> findByAmbulanceIdOrderByRecordedAtDesc(Long ambulanceId);
}
