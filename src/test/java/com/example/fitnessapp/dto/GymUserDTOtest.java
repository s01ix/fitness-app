package com.example.fitnessapp.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GymUserDTOtest {

    @Test
    void testGymUserDTOCreationAndGetters() {
        int expectedId = 15;
        String expectedFullName = "Anna Kowalska";

        GymUserDTO userDTO = new GymUserDTO(expectedId, expectedFullName);

        assertEquals(expectedId, userDTO.getId(), "Błąd: ID powinno odpowiadać wartości z konstruktora");
        assertEquals(expectedFullName, userDTO.getFullName(), "Błąd: Imię i nazwisko powinno odpowiadać wartości z konstruktora");
    }
}