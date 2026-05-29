package com.bloodbank.bloodbank.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmergencyContactRequest {

    @NotBlank(message = "Contact name is required")
    private String contactName;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    private String relationship;

    private Boolean isPrimary = false;

    private Boolean notifyOnEmergency = true;
}
