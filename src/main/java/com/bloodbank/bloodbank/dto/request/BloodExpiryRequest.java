package com.bloodbank.bloodbank.dto.request;

import com.bloodbank.bloodbank.enums.BloodGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BloodExpiryRequest {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Units is required")
    @Min(value = 1, message = "At least 1 unit")
    private Integer units;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private LocalDate collectionDate;
    private String bagNumber;
    private String donorName;
}
