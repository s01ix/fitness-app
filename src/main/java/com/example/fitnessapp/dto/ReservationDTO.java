package com.example.fitnessapp.dto;

import java.time.LocalDate;

public class ReservationDTO {
    private final int id;
    private final String type;
    private final String eventName;
    private final LocalDate date;
    private final String status;

    public ReservationDTO(int id, String type, String eventName, LocalDate date, String status) {
        this.id = id;
        this.type = type;
        this.eventName = eventName;
        this.date = date;
        this.status = status;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getEventName() { return eventName; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
}