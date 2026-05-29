package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.response.BloodSearchResponse;
import com.bloodbank.bloodbank.enums.BloodGroup;

import java.util.List;
import java.util.Map;

public interface VoiceSearchService {

    // Parse voice text → extract blood group + location
    Map<String, String> parseVoiceInput(String voiceText);

    // Search blood based on voice text
    List<BloodSearchResponse> searchByVoice(String voiceText, Double latitude, Double longitude);
}
