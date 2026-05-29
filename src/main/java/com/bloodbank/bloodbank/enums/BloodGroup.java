package com.bloodbank.bloodbank.enums;

public enum BloodGroup {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-");

    private final String label;

    BloodGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BloodGroup fromLabel(String label) {
        for (BloodGroup bg : values()) {
            if (bg.label.equalsIgnoreCase(label)) return bg;
        }
        throw new IllegalArgumentException("Invalid blood group: " + label);
    }
}
