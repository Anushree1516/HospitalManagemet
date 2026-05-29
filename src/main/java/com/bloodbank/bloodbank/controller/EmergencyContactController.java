package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.request.EmergencyContactRequest;
import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.entity.EmergencyContact;
import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.EmergencyContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emergency-contacts")
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;
    private final UserRepository userRepository;

    /**
     * POST /api/v1/emergency-contacts
     * Add emergency contact for logged-in user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmergencyContact>> addContact(
            @Valid @RequestBody EmergencyContactRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        EmergencyContact contact = emergencyContactService.addContact(user.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Emergency contact added", contact));
    }

    /**
     * GET /api/v1/emergency-contacts
     * Get all emergency contacts of logged-in user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmergencyContact>>> getMyContacts(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<EmergencyContact> contacts = emergencyContactService.getUserContacts(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Your emergency contacts", contacts));
    }

    /**
     * GET /api/v1/emergency-contacts/primary
     * Get primary emergency contact
     */
    @GetMapping("/primary")
    public ResponseEntity<ApiResponse<EmergencyContact>> getPrimaryContact(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        EmergencyContact contact = emergencyContactService.getPrimaryContact(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Primary emergency contact", contact));
    }

    /**
     * PUT /api/v1/emergency-contacts/{id}
     * Update an emergency contact
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmergencyContact>> updateContact(
            @PathVariable Long id,
            @Valid @RequestBody EmergencyContactRequest request) {

        EmergencyContact contact = emergencyContactService.updateContact(id, request);
        return ResponseEntity.ok(ApiResponse.success("Emergency contact updated", contact));
    }

    /**
     * DELETE /api/v1/emergency-contacts/{id}
     * Delete an emergency contact
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long id) {
        emergencyContactService.deleteContact(id);
        return ResponseEntity.ok(ApiResponse.success("Emergency contact deleted"));
    }
}
