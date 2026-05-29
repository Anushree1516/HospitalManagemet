package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.BloodRequestDto;
import com.bloodbank.bloodbank.entity.BloodRequest;
import com.bloodbank.bloodbank.enums.RequestStatus;

import java.util.List;

public interface BloodRequestService {

    BloodRequest createRequest(Long userId, BloodRequestDto dto);

    BloodRequest updateStatus(Long requestId, RequestStatus status);

    List<BloodRequest> getRequestsByUser(Long userId);

    List<BloodRequest> getRequestsByHospital(Long hospitalId);

    List<BloodRequest> getEmergencyRequests();

    List<BloodRequest> getAllRequests();

    BloodRequest getById(Long id);
}
