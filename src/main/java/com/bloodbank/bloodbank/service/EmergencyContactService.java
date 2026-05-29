package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.EmergencyContactRequest;
import com.bloodbank.bloodbank.entity.EmergencyContact;

import java.util.List;

public interface EmergencyContactService {

    EmergencyContact addContact(Long userId, EmergencyContactRequest request);

    EmergencyContact updateContact(Long contactId, EmergencyContactRequest request);

    void deleteContact(Long contactId);

    List<EmergencyContact> getUserContacts(Long userId);

    EmergencyContact getPrimaryContact(Long userId);
}
