package com.example.fitnessapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExerciseDictTest {

    @Test
    public void testExerciseDictConstructorAndGetters() {
        int id = 1;
        String name = "Wyciskanie sztangi";
        String desc = "Klasyczne wyciskanie na ławce płaskiej";
        String muscle = "Klatka piersiowa";
        ExerciseDict exercise = new ExerciseDict(id, name, desc, muscle);
        assertEquals(id, exercise.getId());
        assertEquals(name, exercise.getName());
        assertEquals(desc, exercise.getDescription());
        assertEquals(muscle, exercise.getMuscleGroup());
    }

    @Test
    public void testExerciseDictSetters() {
        ExerciseDict exercise = new ExerciseDict();
        exercise.setId(2);
        exercise.setName("Przysiad");
        exercise.setDescription("Przysiad ze sztangą na karku");
        exercise.setMuscleGroup("Nogi");
        assertEquals(2, exercise.getId());
        assertEquals("Przysiad", exercise.getName());
        assertEquals("Przysiad ze sztangą na karku", exercise.getDescription());
        assertEquals("Nogi", exercise.getMuscleGroup());
    }
}