package com.example.fitnessapp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationTest {

    //test anulowania rezerwacji
    @Test
    void testCanBeCanceled_BusinessLogic() {

        Reservation reservation = new Reservation();
        reservation.setReservationDate(LocalDate.of(2026, 8, 15));
        reservation.setStatus("CONFIRMED");

        assertTrue(reservation.canBeCanceled(LocalDate.of(2026, 8, 10)), "Powinno pozwolić na anulowanie z wyprzedzeniem");

        assertFalse(reservation.canBeCanceled(LocalDate.of(2026, 8, 15)), "Nie powinno pozwolić na anulowanie w dniu wydarzenia");

        assertFalse(reservation.canBeCanceled(LocalDate.of(2026, 8, 16)), "Nie powinno pozwolić na anulowanie po dacie");

        reservation.setStatus("CANCELED");
        assertFalse(reservation.canBeCanceled(LocalDate.of(2026, 8, 10)), "Nie można anulować rezerwacji o statusie innym niż CONFIRMED");
    }
}
