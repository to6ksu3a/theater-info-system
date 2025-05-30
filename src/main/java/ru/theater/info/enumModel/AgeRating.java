package ru.theater.info.enumModel;

public enum AgeRating {
    ZERO("0+", 0),
    SIX("6+", 6),
    TWELVE("12+", 12),
    SIXTEEN("16+", 16),
    EIGHTEEN("18+", 18);

    private final String displayName;
    private final int value;

    AgeRating(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValue() {
        return value;
    }

    public static AgeRating fromDisplayName(String displayName) {
        for (AgeRating rating : values()) {
            if (rating.displayName.equals(displayName)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Неизвестный рейтинг: " + displayName);
    }
}