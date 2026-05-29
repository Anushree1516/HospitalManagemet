package com.bloodbank.bloodbank.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationUpdateRequest {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private Double speedKmh;
    private Double headingDegrees;
    private Double accuracyMeters;
    private String address;

    // Optional: link to a blood request
    private Long bloodRequestId;

    // Type: AMBULANCE, PATIENT, DONOR
    private String trackingType;
}
