package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    List<EmergencyContact> findByUserId(Long userId);

    Optional<EmergencyContact> findByUserIdAndIsPrimary(Long userId, Boolean isPrimary);

    List<EmergencyContact> findByUserIdAndNotifyOnEmergency(Long userId, Boolean notify);

    long countByUserId(Long userId);
}
