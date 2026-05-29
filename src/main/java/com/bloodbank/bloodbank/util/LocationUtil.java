package com.bloodbank.bloodbank.util;

import org.springframework.stereotype.Component;

@Component
public class LocationUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two coordinates using Haversine formula
     * @return distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Generate Google Maps URL for a location
     */
    public String generateGoogleMapsUrl(double latitude, double longitude) {
        return String.format("https://www.google.com/maps?q=%f,%f", latitude, longitude);
    }

    /**
     * Generate Google Maps directions URL from user to hospital
     */
    public String generateDirectionsUrl(double fromLat, double fromLng,
                                        double toLat, double toLng) {
        return String.format(
                "https://www.google.com/maps/dir/%f,%f/%f,%f",
                fromLat, fromLng, toLat, toLng
        );
    }
}
