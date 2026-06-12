package com.example.fitnessapp.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testMessageConstructorAndGetters() {
        // Przygotowanie danych
        int expectedId = 1;
        int expectedSenderId = 10;
        int expectedReceiverId = 23;
        String expectedContent = "Pamiętaj o wodzie na trening!";
        LocalDateTime expectedDate = LocalDateTime.of(2026, 6, 11, 10, 0);

        // Utworzenie obiektu
        Message message = new Message(expectedId, expectedSenderId, expectedReceiverId, expectedContent, expectedDate);

        // Sprawdzenie poprawności
        assertEquals(expectedId, message.getId());
        assertEquals(expectedSenderId, message.getSenderId());
        assertEquals(expectedReceiverId, message.getReceiverId());
        assertEquals(expectedContent, message.getContent());
        assertEquals(expectedDate, message.getSentAt());
    }

    @Test
    void testMessageSetters() {
        // Utworzenie pustego obiektu
        Message message = new Message();

        // Ustawienie danych za pomocą setterów
        message.setId(5);
        message.setSenderId(12);
        message.setReceiverId(8);
        message.setContent("Nowa wiadomość");

        // Sprawdzenie poprawności
        assertEquals(5, message.getId());
        assertEquals(12, message.getSenderId());
        assertEquals(8, message.getReceiverId());
        assertEquals("Nowa wiadomość", message.getContent());
    }
}