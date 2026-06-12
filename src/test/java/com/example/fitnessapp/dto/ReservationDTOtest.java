package com.example.fitnessapp.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ReservationDTOtest {

    @Test
    void testReservationDTOCreationAndGetters() {

        int expectedId = 100;
        String expectedType = "ZAJĘCIA GRUPOWE";
        String expectedEventName = "Joga dla początkujących";
        LocalDate expectedDate = LocalDate.of(2026, 8, 15);
        String expectedStatus = "CONFIRMED";

        ReservationDTO reservationDTO = new ReservationDTO(
                expectedId, expectedType, expectedEventName, expectedDate, expectedStatus
        );

        assertEquals(expectedId, reservationDTO.getId());
        assertEquals(expectedType, reservationDTO.getType());
        assertEquals(expectedEventName, reservationDTO.getEventName());
        assertEquals(expectedDate, reservationDTO.getDate());
        assertEquals(expectedStatus, reservationDTO.getStatus());
    }
}