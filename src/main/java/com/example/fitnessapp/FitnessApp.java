package com.example.fitnessapp;
import com.example.fitnessapp.database.DatabaseInitializer;

import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.database.DatabaseInitializer;
import com.example.fitnessapp.model.*;
import java.time.LocalDate;
import java.util.List;

public class FitnessApp {
    public static void main(String[] args){
        DatabaseInitializer.init();
    }

}

