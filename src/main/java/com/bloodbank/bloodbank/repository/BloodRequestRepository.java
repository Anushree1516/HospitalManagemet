package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.BloodRequest;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findByRequestedById(Long userId);

    List<BloodRequest> findByHospitalId(Long hospitalId);

    List<BloodRequest> findByStatus(RequestStatus status);

    List<BloodRequest> findByStatusAndIsEmergency(RequestStatus status, Boolean isEmergency);

    List<BloodRequest> findByBloodGroupAndStatus(BloodGroup bloodGroup, RequestStatus status);

    // Emergency requests ordered by latest
    List<BloodRequest> findByIsEmergencyTrueAndStatusOrderByCreatedAtDesc(RequestStatus status);

    long countByStatus(RequestStatus status);

    long countByIsEmergencyTrue();
}
