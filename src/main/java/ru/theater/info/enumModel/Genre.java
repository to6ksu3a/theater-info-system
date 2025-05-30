package ru.theater.info.enumModel;

public enum Genre {
    DRAMA("Драма"),
    COMEDY("Комедия"),
    TRAGEDY("Трагедия"),
    MUSICAL("Мюзикл"),
    OPERA("Опера"),
    BALLET("Балет");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}