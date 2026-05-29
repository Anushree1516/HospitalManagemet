package com.bloodbank.bloodbank.dto.response;

import com.bloodbank.bloodbank.enums.BloodGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodSearchResponse {

    private Long hospitalId;
    private String hospitalName;
    private String address;
    private String city;
    private String state;
    private String phoneNumber;
    private BloodGroup bloodGroup;
    private Integer availableUnits;
    private Double latitude;
    private Double longitude;
    private Double distanceKm; // calculated from user location
    private String googleMapsUrl; // Direct Google Maps link
}
