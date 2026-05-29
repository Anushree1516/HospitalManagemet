package com.bloodbank.bloodbank.dto.request;

import com.bloodbank.bloodbank.enums.BloodGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BloodRequestDto {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Units required")
    @Min(value = 1, message = "At least 1 unit required")
    private Integer unitsRequired;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    private Integer patientAge;

    private String reason;

    private Boolean isEmergency = false;

    private Long hospitalId;

    // Patient location for ambulance dispatch
    private Double patientLatitude;
    private Double patientLongitude;
    private String patientAddress;

    private LocalDateTime requiredBy;
}
