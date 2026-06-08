package com.example.fitnessapp.dto;

public class GymUserDTO {
    private final int id;
    private final String fullName;

    public GymUserDTO(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
}