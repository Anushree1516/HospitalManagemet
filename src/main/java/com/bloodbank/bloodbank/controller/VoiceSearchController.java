package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.dto.response.ApiResponse;
import com.bloodbank.bloodbank.dto.response.BloodSearchResponse;
import com.bloodbank.bloodbank.service.MultiLanguageService;
import com.bloodbank.bloodbank.service.VoiceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VoiceSearchController {

    private final VoiceSearchService voiceSearchService;
    private final MultiLanguageService multiLanguageService;

    /**
     * POST /api/v1/voice-search
     * Search blood using voice text input
     * Mobile app converts speech to text → sends here
     *
     * Examples:
     * "Find A positive blood near me"
     * "I need O negative blood in Chennai urgently"
     * "AB positive blood needed immediately"
     */
    @PostMapping("/voice-search")
    public ResponseEntity<ApiResponse<List<BloodSearchResponse>>> voiceSearch(
            @RequestParam String text,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "en") String lang) {

        // Parse voice input first
        Map<String, String> parsed = voiceSearchService.parseVoiceInput(text);

        // Search blood
        List<BloodSearchResponse> results = voiceSearchService.searchByVoice(text, lat, lng);

        // Return response in requested language
        String message = multiLanguageService.getMessage(
                results.isEmpty()
                        ? MultiLanguageService.MSG_BLOOD_NOT_FOUND
                        : MultiLanguageService.MSG_BLOOD_FOUND,
                lang
        );

        return ResponseEntity.ok(ApiResponse.<List<BloodSearchResponse>>builder()
                .success(true)
                .message(message)
                .data(results)
                .build());
    }

    /**
     * GET /api/v1/voice-search/parse?text=Find A positive blood in Chennai
     * Just parse voice text without searching (for testing)
     */
    @GetMapping("/voice-search/parse")
    public ResponseEntity<ApiResponse<Map<String, String>>> parseVoice(
            @RequestParam String text) {

        Map<String, String> parsed = voiceSearchService.parseVoiceInput(text);
        return ResponseEntity.ok(ApiResponse.success("Voice input parsed", parsed));
    }

    /**
     * GET /api/v1/languages
     * Get all supported languages
     */
    @GetMapping("/languages")
    public ResponseEntity<ApiResponse<List<String>>> getSupportedLanguages() {
        return ResponseEntity.ok(ApiResponse.success(
                "Supported languages", multiLanguageService.getSupportedLanguages()));
    }

    /**
     * GET /api/v1/messages?key=blood.found&lang=ta
     * Get a message in a specific language (for mobile app)
     */
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<String>> getMessage(
            @RequestParam String key,
            @RequestParam(defaultValue = "en") String lang) {

        String message = multiLanguageService.getMessage(key, lang);
        return ResponseEntity.ok(ApiResponse.success("Message", message));
    }
}
