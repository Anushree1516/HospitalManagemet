package com.bloodbank.bloodbank.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AmbulanceRequest {

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "Driver name is required")
    private String driverName;

    @NotBlank(message = "Driver phone is required")
    private String driverPhone;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private Long hospitalId;

    // Initial location
    private Double currentLatitude;
    private Double currentLongitude;
}
