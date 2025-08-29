package com.tracker.job_ts.WeeklyBoard.entity;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum BoardDay {
    MONDAY(1, "MONDAY", "Pazartesi"),
    TUESDAY(2, "TUESDAY", "Salı"),
    WEDNESDAY(3, "WEDNESDAY", "Çarşamba"),
    THURSDAY(4, "THURSDAY", "Perşembe"),
    FRIDAY(5, "FRIDAY", "Cuma"),
    SATURDAY(6, "SATURDAY", "Cumartesi"),
    SUNDAY(7, "SUNDAY", "Pazar");

    private final int id;
    private final String enValue;
    private final String trValue;

    BoardDay(int id, String enValue, String trValue) {
        this.id = id;
        this.enValue = enValue;
        this.trValue = trValue;
    }

    public int getId() {
        return id;
    }

    public String getEnValue() {
        return enValue;
    }

    public String getTrValue() {
        return trValue;
    }

    // ID'ye göre enum sabitini bulmak için yardımcı metot
    public static BoardDay fromId(int id) {
        return Arrays.stream(BoardDay.values())
                .filter(day -> day.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No enum constant found with ID: " + id));
    }
}