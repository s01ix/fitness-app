package com.example.fitnessapp.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class GymPassTest {

    @Test
    void testGymPassCreation() {

        int expectedId = 1;
        int expectedPassTypeId = 5;
        int expectedUserId = 23;
        BigDecimal expectedPrice = new BigDecimal("150.00");
        LocalDate expectedPurchaseDate = LocalDate.of(2026, 6, 1);
        LocalDate expectedExpirationDate = LocalDate.of(2026, 7, 1);
        String expectedStatus = "ACTIVE";

        GymPass gymPass = new GymPass(expectedId, expectedUserId, expectedPassTypeId, expectedPrice, expectedPurchaseDate, expectedExpirationDate, expectedStatus);

        assertEquals(expectedId, gymPass.getId());
        assertEquals(expectedPassTypeId, gymPass.getPassTypeId());
        assertEquals(expectedUserId, gymPass.getUserId());
        assertEquals(expectedPrice, gymPass.getPrice());
        assertEquals(expectedPurchaseDate, gymPass.getPurchaseDate());
        assertEquals(expectedExpirationDate, gymPass.getExpirationDate());
        assertEquals(expectedStatus, gymPass.getStatus());
    }

    //test ważności dat karnetu
    @Test
    void testIsPassActiveOn_BusinessLogic() {
        java.time.LocalDate start = java.time.LocalDate.of(2026, 6, 1);
        java.time.LocalDate end = java.time.LocalDate.of(2026, 6, 30);
        GymPass pass = new GymPass(1, 5, 23, new java.math.BigDecimal("100"), start, end, "ACTIVE");

        assertTrue(pass.isPassActiveOn(java.time.LocalDate.of(2026, 6, 15)));

        assertTrue(pass.isPassActiveOn(start));

        assertFalse(pass.isPassActiveOn(java.time.LocalDate.of(2026, 5, 31)));

        assertFalse(pass.isPassActiveOn(java.time.LocalDate.of(2026, 7, 1)));

        pass.setStatus("INACTIVE");
        assertFalse(pass.isPassActiveOn(java.time.LocalDate.of(2026, 6, 15)));
    }

    //test liczenia dni do końca karnetu
    @Test
    void testGetDaysRemaining_BusinessLogic() {
        GymPass pass = new GymPass();
        pass.setExpirationDate(LocalDate.of(2026, 6, 20)); // Karnet ważny do 20 czerwca

        assertEquals(10, pass.getDaysRemaining(LocalDate.of(2026, 6, 10)));

        assertEquals(1, pass.getDaysRemaining(LocalDate.of(2026, 6, 19)));

        assertEquals(0, pass.getDaysRemaining(LocalDate.of(2026, 6, 20)));

        assertEquals(0, pass.getDaysRemaining(LocalDate.of(2026, 6, 25)));
    }
}