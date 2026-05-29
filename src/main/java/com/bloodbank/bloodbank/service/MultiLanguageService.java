package com.bloodbank.bloodbank.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MultiLanguageService {

    // Supported languages
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_TAMIL   = "ta";
    public static final String LANG_HINDI   = "hi";
    public static final String LANG_TELUGU  = "te";
    public static final String LANG_KANNADA = "kn";

    // Message keys
    public static final String MSG_LOGIN_SUCCESS     = "login.success";
    public static final String MSG_REGISTER_SUCCESS  = "register.success";
    public static final String MSG_BLOOD_FOUND       = "blood.found";
    public static final String MSG_BLOOD_NOT_FOUND   = "blood.not_found";
    public static final String MSG_REQUEST_CREATED   = "request.created";
    public static final String MSG_REQUEST_APPROVED  = "request.approved";
    public static final String MSG_REQUEST_FULFILLED = "request.fulfilled";
    public static final String MSG_EMERGENCY_ALERT   = "emergency.alert";
    public static final String MSG_DONOR_REGISTERED  = "donor.registered";
    public static final String MSG_STOCK_UPDATED     = "stock.updated";
    public static final String MSG_AMBULANCE_DISPATCHED = "ambulance.dispatched";

    private static final Map<String, Map<String, String>> MESSAGES = new HashMap<>();

    static {
        // ── ENGLISH ────────────────────────────────────────
        Map<String, String> en = new HashMap<>();
        en.put(MSG_LOGIN_SUCCESS,       "Login successful");
        en.put(MSG_REGISTER_SUCCESS,    "User registered successfully");
        en.put(MSG_BLOOD_FOUND,         "Blood search results found");
        en.put(MSG_BLOOD_NOT_FOUND,     "No blood available in this area");
        en.put(MSG_REQUEST_CREATED,     "Blood request created successfully");
        en.put(MSG_REQUEST_APPROVED,    "Your blood request has been approved");
        en.put(MSG_REQUEST_FULFILLED,   "Blood request fulfilled successfully");
        en.put(MSG_EMERGENCY_ALERT,     "Emergency blood request! Donors notified nearby");
        en.put(MSG_DONOR_REGISTERED,    "You are registered as a blood donor");
        en.put(MSG_STOCK_UPDATED,       "Blood stock updated successfully");
        en.put(MSG_AMBULANCE_DISPATCHED,"Ambulance dispatched to your location");
        MESSAGES.put(LANG_ENGLISH, en);

        // ── TAMIL ──────────────────────────────────────────
        Map<String, String> ta = new HashMap<>();
        ta.put(MSG_LOGIN_SUCCESS,       "உள்நுழைவு வெற்றிகரமாக உள்ளது");
        ta.put(MSG_REGISTER_SUCCESS,    "பயனர் வெற்றிகரமாக பதிவு செய்யப்பட்டார்");
        ta.put(MSG_BLOOD_FOUND,         "இரத்த தேடல் முடிவுகள் கிடைத்தன");
        ta.put(MSG_BLOOD_NOT_FOUND,     "இந்த பகுதியில் இரத்தம் கிடைக்கவில்லை");
        ta.put(MSG_REQUEST_CREATED,     "இரத்த கோரிக்கை வெற்றிகரமாக உருவாக்கப்பட்டது");
        ta.put(MSG_REQUEST_APPROVED,    "உங்கள் இரத்த கோரிக்கை அங்கீகரிக்கப்பட்டது");
        ta.put(MSG_REQUEST_FULFILLED,   "இரத்த கோரிக்கை நிறைவேற்றப்பட்டது");
        ta.put(MSG_EMERGENCY_ALERT,     "அவசர இரத்த கோரிக்கை! அருகிலுள்ள தானியாளர்களுக்கு அறிவிக்கப்பட்டது");
        ta.put(MSG_DONOR_REGISTERED,    "நீங்கள் இரத்த தானியாளராக பதிவு செய்யப்பட்டீர்கள்");
        ta.put(MSG_STOCK_UPDATED,       "இரத்த இருப்பு வெற்றிகரமாக புதுப்பிக்கப்பட்டது");
        ta.put(MSG_AMBULANCE_DISPATCHED,"ஆம்புலன்ஸ் உங்கள் இடத்திற்கு அனுப்பப்பட்டது");
        MESSAGES.put(LANG_TAMIL, ta);

        // ── HINDI ──────────────────────────────────────────
        Map<String, String> hi = new HashMap<>();
        hi.put(MSG_LOGIN_SUCCESS,       "लॉगिन सफल रहा");
        hi.put(MSG_REGISTER_SUCCESS,    "उपयोगकर्ता सफलतापूर्वक पंजीकृत हुआ");
        hi.put(MSG_BLOOD_FOUND,         "रक्त खोज परिणाम मिले");
        hi.put(MSG_BLOOD_NOT_FOUND,     "इस क्षेत्र में रक्त उपलब्ध नहीं है");
        hi.put(MSG_REQUEST_CREATED,     "रक्त अनुरोध सफलतापूर्वक बनाया गया");
        hi.put(MSG_REQUEST_APPROVED,    "आपका रक्त अनुरोध स्वीकृत हो गया");
        hi.put(MSG_REQUEST_FULFILLED,   "रक्त अनुरोध पूरा किया गया");
        hi.put(MSG_EMERGENCY_ALERT,     "आपातकालीन रक्त अनुरोध! नजदीकी दाताओं को सूचित किया गया");
        hi.put(MSG_DONOR_REGISTERED,    "आप रक्त दाता के रूप में पंजीकृत हो गए");
        hi.put(MSG_STOCK_UPDATED,       "रक्त स्टॉक सफलतापूर्वक अपडेट किया गया");
        hi.put(MSG_AMBULANCE_DISPATCHED,"एम्बुलेंस आपके स्थान पर भेजी गई");
        MESSAGES.put(LANG_HINDI, hi);

        // ── TELUGU ─────────────────────────────────────────
        Map<String, String> te = new HashMap<>();
        te.put(MSG_LOGIN_SUCCESS,       "లాగిన్ విజయవంతమైంది");
        te.put(MSG_REGISTER_SUCCESS,    "వినియోగదారు విజయవంతంగా నమోదు అయ్యారు");
        te.put(MSG_BLOOD_FOUND,         "రక్తం అన్వేషణ ఫలితాలు దొరికాయి");
        te.put(MSG_BLOOD_NOT_FOUND,     "ఈ ప్రాంతంలో రక్తం అందుబాటులో లేదు");
        te.put(MSG_REQUEST_CREATED,     "రక్తం అభ్యర్థన విజయవంతంగా సృష్టించబడింది");
        te.put(MSG_AMBULANCE_DISPATCHED,"అంబులెన్స్ మీ స్థానానికి పంపబడింది");
        MESSAGES.put(LANG_TELUGU, te);

        // ── KANNADA ────────────────────────────────────────
        Map<String, String> kn = new HashMap<>();
        kn.put(MSG_LOGIN_SUCCESS,       "ಲಾಗಿನ್ ಯಶಸ್ವಿಯಾಗಿದೆ");
        kn.put(MSG_REGISTER_SUCCESS,    "ಬಳಕೆದಾರರು ಯಶಸ್ವಿಯಾಗಿ ನೋಂದಾಯಿಸಿದ್ದಾರೆ");
        kn.put(MSG_BLOOD_FOUND,         "ರಕ್ತ ಹುಡುಕಾಟ ಫಲಿತಾಂಶಗಳು ಸಿಕ್ಕಿವೆ");
        kn.put(MSG_BLOOD_NOT_FOUND,     "ಈ ಪ್ರದೇಶದಲ್ಲಿ ರಕ್ತ ಲಭ್ಯವಿಲ್ಲ");
        kn.put(MSG_AMBULANCE_DISPATCHED,"ಆಂಬ್ಯುಲೆನ್ಸ್ ನಿಮ್ಮ ಸ್ಥಳಕ್ಕೆ ಕಳುಹಿಸಲಾಗಿದೆ");
        MESSAGES.put(LANG_KANNADA, kn);
    }

    public String getMessage(String key, String language) {
        String lang = (language != null) ? language.toLowerCase() : LANG_ENGLISH;
        Map<String, String> langMessages = MESSAGES.getOrDefault(lang, MESSAGES.get(LANG_ENGLISH));
        return langMessages.getOrDefault(key, MESSAGES.get(LANG_ENGLISH).get(key));
    }

    public String getDefaultMessage(String key) {
        return getMessage(key, LANG_ENGLISH);
    }

    public List<String> getSupportedLanguages() {
        return List.of("en - English", "ta - Tamil", "hi - Hindi", "te - Telugu", "kn - Kannada");
    }
}
