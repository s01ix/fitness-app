package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.ClubDAO;
import com.example.fitnessapp.model.Club;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClubCommandHandlerTest {

    @Mock
    private ClubDAO clubDao;

    private ClubCommandHandler handler;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ClubCommandHandler(clubDao);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    void testAddClubSuccess() {
        String[] tokens = {"ADD_CLUB", "FitGym Centrum", "ul. Sportowa 15", "6:00-22:00"};

        doAnswer(invocation -> {
            Club club = invocation.getArgument(0);
            club.setId(5);
            return null;
        }).when(clubDao).save(any(Club.class));

        boolean result = handler.handle("ADD_CLUB", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_CLUB_OK"));
        assertTrue(output.contains("Klub dodany"));

        ArgumentCaptor<Club> captor = ArgumentCaptor.forClass(Club.class);
        verify(clubDao).save(captor.capture());

        Club saved = captor.getValue();
        assertEquals("FitGym Centrum", saved.getName());
        assertEquals("ul. Sportowa 15", saved.getAddress());
        assertEquals("6:00-22:00", saved.getOpeningHours());
    }

    @Test
    void testAddClubInsufficientData() {
        String[] tokens = {"ADD_CLUB", "FitGym"};

        boolean result = handler.handle("ADD_CLUB", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_CLUB_ERROR"));
        assertTrue(output.contains("Za malo danych"));

        verify(clubDao, never()).save(any());
    }

    @Test
    void testAddClubEmptyName() {
        String[] tokens = {"ADD_CLUB", "  ", "ul. Sportowa 15", "6:00-22:00"};

        boolean result = handler.handle("ADD_CLUB", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_CLUB_ERROR"));
        assertTrue(output.contains("Nazwa jest wymagana"));

        verify(clubDao, never()).save(any());
    }

    @Test
    void testAddClubEmptyAddress() {
        String[] tokens = {"ADD_CLUB", "FitGym", "", "6:00-22:00"};

        boolean result = handler.handle("ADD_CLUB", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_CLUB_ERROR"));
        assertTrue(output.contains("Adres jest wymagany"));

        verify(clubDao, never()).save(any());
    }

    @Test
    void testAddClubWithEmptyOpeningHours() {
        String[] tokens = {"ADD_CLUB", "FitGym", "ul. Sportowa 15", ""};

        doAnswer(invocation -> {
            Club club = invocation.getArgument(0);
            club.setId(7);
            return null;
        }).when(clubDao).save(any(Club.class));

        boolean result = handler.handle("ADD_CLUB", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_CLUB_OK"));

        ArgumentCaptor<Club> captor = ArgumentCaptor.forClass(Club.class);
        verify(clubDao).save(captor.capture());

        Club saved = captor.getValue();
        assertEquals("", saved.getOpeningHours());
    }

    @Test
    void testGetClubsSuccess() {
        List<Club> clubs = Arrays.asList(
                new Club(1, "FitGym Centrum", "ul. Sportowa 15", "6:00-22:00"),
                new Club(2, "FitGym Południe", "ul. Główna 20", "7:00-21:00"),
                new Club(3, "FitGym Północ", "ul. Leśna 5", "5:00-23:00")
        );

        when(clubDao.findAll()).thenReturn(clubs);

        String[] tokens = {"GET_CLUBS"};
        boolean result = handler.handle("GET_CLUBS", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("CLUBS_OK"));
        assertTrue(output.contains("FitGym Centrum"));
        assertTrue(output.contains("ul. Sportowa 15"));
        assertTrue(output.contains("6:00-22:00"));
        assertTrue(output.contains("FitGym Południe"));
        assertTrue(output.contains("FitGym Północ"));

        verify(clubDao).findAll();
    }

    @Test
    void testUnhandledCommand() {
        String[] tokens = {"DELETE_CLUB", "1"};

        boolean result = handler.handle("DELETE_CLUB", tokens, printWriter);

        assertFalse(result);
        verify(clubDao, never()).save(any());
        verify(clubDao, never()).findAll();
    }

    @Test
    void testAddClubDatabaseException() {
        String[] tokens = {"ADD_CLUB", "FitGym", "ul. Sportowa 15", "6:00-22:00"};

        doThrow(new RuntimeException("Connection timeout"))
                .when(clubDao).save(any(Club.class));

        boolean result = handler.handle("ADD_CLUB", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_CLUB_ERROR"));
        assertTrue(output.contains("Connection timeout"));
    }

    @Test
    void testGetClubsResponseFormat() {
        List<Club> clubs = Arrays.asList(
                new Club(1, "FitGym", "ul. Sportowa 15", "6:00-22:00")
        );

        when(clubDao.findAll()).thenReturn(clubs);

        String[] tokens = {"GET_CLUBS"};
        handler.handle("GET_CLUBS", tokens, printWriter);

        String output = stringWriter.toString().trim();
        assertTrue(output.matches("CLUBS_OK;\\d+,.*,.*,.*"));
        assertTrue(output.contains("1,FitGym,ul. Sportowa 15,6:00-22:00"));
    }
}