package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.request.EmergencyContactRequest;
import com.bloodbank.bloodbank.entity.EmergencyContact;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.bloodbank.repository.EmergencyContactRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.EmergencyContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyContactServiceImpl implements EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EmergencyContact addContact(Long userId, EmergencyContactRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Max 3 emergency contacts per user
        if (emergencyContactRepository.countByUserId(userId) >= 3) {
            throw new IllegalArgumentException("Maximum 3 emergency contacts allowed");
        }

        // If this is primary, remove primary from others
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            emergencyContactRepository.findByUserIdAndIsPrimary(userId, true)
                    .ifPresent(existing -> {
                        existing.setIsPrimary(false);
                        emergencyContactRepository.save(existing);
                    });
        }

        EmergencyContact contact = EmergencyContact.builder()
                .user(user)
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .relationship(request.getRelationship())
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .notifyOnEmergency(request.getNotifyOnEmergency() != null ? request.getNotifyOnEmergency() : true)
                .build();

        return emergencyContactRepository.save(contact);
    }

    @Override
    @Transactional
    public EmergencyContact updateContact(Long contactId, EmergencyContactRequest request) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency contact not found"));

        contact.setContactName(request.getContactName());
        contact.setContactPhone(request.getContactPhone());
        contact.setRelationship(request.getRelationship());
        if (request.getIsPrimary() != null) contact.setIsPrimary(request.getIsPrimary());
        if (request.getNotifyOnEmergency() != null) contact.setNotifyOnEmergency(request.getNotifyOnEmergency());

        return emergencyContactRepository.save(contact);
    }

    @Override
    @Transactional
    public void deleteContact(Long contactId) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency contact not found"));
        emergencyContactRepository.delete(contact);
    }

    @Override
    public List<EmergencyContact> getUserContacts(Long userId) {
        return emergencyContactRepository.findByUserId(userId);
    }

    @Override
    public EmergencyContact getPrimaryContact(Long userId) {
        return emergencyContactRepository.findByUserIdAndIsPrimary(userId, true)
                .orElseThrow(() -> new ResourceNotFoundException("No primary emergency contact set"));
    }
}
