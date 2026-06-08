package com.example.fitnessapp.dto;

import java.time.LocalDateTime;

public class GroupClassDTO {
    private final int id;
    private final String name;
    private final LocalDateTime scheduleTime;
    private final String trainerName;
    private final int capacity;

    public GroupClassDTO(int id, String name, LocalDateTime scheduleTime, String trainerName, int capacity) {
        this.id = id;
        this.name = name;
        this.scheduleTime = scheduleTime;
        this.trainerName = trainerName;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getScheduleTime() { return scheduleTime; }
    public String getTrainerName() { return trainerName; }
    public int getCapacity() { return capacity; }
}
