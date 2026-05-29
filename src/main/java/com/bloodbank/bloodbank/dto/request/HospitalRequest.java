package com.bloodbank.bloodbank.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HospitalRequest {

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    private String registrationNumber;

    @Email(message = "Invalid email")
    private String email;

    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;

    // Google Maps coordinates
    private Double latitude;
    private Double longitude;
}
