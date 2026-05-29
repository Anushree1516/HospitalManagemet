package com.bloodbank.bloodbank.dto.request;

import com.bloodbank.bloodbank.enums.BloodGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DonorRequest {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Donor must be at least 18 years old")
    private Integer age;

    @Min(value = 50, message = "Minimum weight 50 kg required")
    private Double weightKg;

    private Double latitude;
    private Double longitude;
    private String city;

    private Boolean isNotifyEnabled = true;
}
