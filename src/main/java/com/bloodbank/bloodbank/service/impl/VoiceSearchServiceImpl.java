package com.bloodbank.bloodbank.service.impl;

import com.bloodbank.bloodbank.dto.response.BloodSearchResponse;
import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.service.BloodStockService;
import com.bloodbank.bloodbank.service.VoiceSearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VoiceSearchServiceImpl implements VoiceSearchService {

    private static final Logger logger = LoggerFactory.getLogger(VoiceSearchServiceImpl.class);

    private final BloodStockService bloodStockService;

    // Blood group keyword mapping (supports English + transliterated Tamil/Hindi)
    private static final Map<String, BloodGroup> BLOOD_GROUP_KEYWORDS = new HashMap<>();

    static {
        // English
        BLOOD_GROUP_KEYWORDS.put("a positive", BloodGroup.A_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("a+", BloodGroup.A_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("a negative", BloodGroup.A_NEGATIVE);
        BLOOD_GROUP_KEYWORDS.put("a-", BloodGroup.A_NEGATIVE);
        BLOOD_GROUP_KEYWORDS.put("b positive", BloodGroup.B_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("b+", BloodGroup.B_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("b negative", BloodGroup.B_NEGATIVE);
        BLOOD_GROUP_KEYWORDS.put("b-", BloodGroup.B_NEGATIVE);
        BLOOD_GROUP_KEYWORDS.put("o positive", BloodGroup.O_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("o+", BloodGroup.O_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("o negative", BloodGroup.O_NEGATIVE);
        BLOOD_GROUP_KEYWORDS.put("o-", BloodGroup.O_NEGATIVE);
        BLOOD_GROUP_KEYWORDS.put("ab positive", BloodGroup.AB_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("ab+", BloodGroup.AB_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("ab negative", BloodGroup.AB_NEGATIVE);
        BLOOD_GROUP_KEYWORDS.put("ab-", BloodGroup.AB_NEGATIVE);

        // Hindi transliteration
        BLOOD_GROUP_KEYWORDS.put("a positiv", BloodGroup.A_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("o positiv", BloodGroup.O_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("b positiv", BloodGroup.B_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("ab positiv", BloodGroup.AB_POSITIVE);

        // Tamil transliteration
        BLOOD_GROUP_KEYWORDS.put("a mudhal", BloodGroup.A_POSITIVE);
        BLOOD_GROUP_KEYWORDS.put("o mudhal", BloodGroup.O_POSITIVE);
    }

    // City keyword extraction
    private static final Map<String, String> CITY_KEYWORDS = new HashMap<>();
    static {
        CITY_KEYWORDS.put("chennai", "Chennai");
        CITY_KEYWORDS.put("mumbai", "Mumbai");
        CITY_KEYWORDS.put("delhi", "Delhi");
        CITY_KEYWORDS.put("bangalore", "Bangalore");
        CITY_KEYWORDS.put("bengaluru", "Bangalore");
        CITY_KEYWORDS.put("hyderabad", "Hyderabad");
        CITY_KEYWORDS.put("kolkata", "Kolkata");
        CITY_KEYWORDS.put("pune", "Pune");
        CITY_KEYWORDS.put("coimbatore", "Coimbatore");
        CITY_KEYWORDS.put("madurai", "Madurai");
    }

    @Override
    public Map<String, String> parseVoiceInput(String voiceText) {
        Map<String, String> result = new HashMap<>();
        String lowerText = voiceText.toLowerCase().trim();

        logger.info("🎙️ Parsing voice input: {}", voiceText);

        // Extract blood group
        BloodGroup detectedGroup = null;
        for (Map.Entry<String, BloodGroup> entry : BLOOD_GROUP_KEYWORDS.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                detectedGroup = entry.getValue();
                break;
            }
        }

        if (detectedGroup != null) {
            result.put("bloodGroup", detectedGroup.name());
            result.put("bloodGroupLabel", detectedGroup.getLabel());
        } else {
            result.put("bloodGroup", null);
            result.put("error", "Could not detect blood group from: " + voiceText);
        }

        // Extract city
        String detectedCity = null;
        for (Map.Entry<String, String> entry : CITY_KEYWORDS.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                detectedCity = entry.getValue();
                break;
            }
        }

        if (detectedCity != null) {
            result.put("city", detectedCity);
        }

        // Detect emergency keywords
        boolean isEmergency = lowerText.contains("emergency") ||
                lowerText.contains("urgent") ||
                lowerText.contains("immediately") ||
                lowerText.contains("asap") ||
                lowerText.contains("avasaram"); // Tamil for emergency

        result.put("isEmergency", String.valueOf(isEmergency));
        result.put("originalText", voiceText);

        logger.info("🎙️ Parsed → bloodGroup: {}, city: {}, emergency: {}",
                detectedGroup, detectedCity, isEmergency);

        return result;
    }

    @Override
    public List<BloodSearchResponse> searchByVoice(String voiceText,
                                                     Double latitude,
                                                     Double longitude) {
        Map<String, String> parsed = parseVoiceInput(voiceText);

        if (parsed.get("bloodGroup") == null) {
            throw new IllegalArgumentException(
                    "Could not understand blood group from voice input. Please say something like: " +
                            "'Find A positive blood near me' or 'I need O negative blood in Chennai'"
            );
        }

        BloodGroup bloodGroup = BloodGroup.valueOf(parsed.get("bloodGroup"));

        // If location provided → nearby search
        if (latitude != null && longitude != null) {
            return bloodStockService.searchNearbyBlood(bloodGroup, latitude, longitude, 15.0);
        }

        // If city detected → city search
        if (parsed.get("city") != null) {
            return bloodStockService.searchBloodByGroupAndCity(bloodGroup, parsed.get("city"));
        }

        throw new IllegalArgumentException(
                "Please provide your location or mention a city in your voice search."
        );
    }
}
