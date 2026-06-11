package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReceptionistCommandHandlerTest {
    private ReceptionistCommandHandler handler;
    private PassTypeDAO mockPassTypeDao;
    private GymPassDAO mockGymPassDao;
    private PaymentDAO mockPaymentDao;
    private GroupClassDAO mockGroupClassDao;
    private ReservationDAO mockReservationDao;
    private GymUserDAO mockUserDao;
    private ClubDAO mockClubDao;
    private MessageDAO mockMessageDao;
    
    private StringWriter sw;
    private PrintWriter pw;

    @BeforeEach
    public void setUp() {
        mockPassTypeDao = mock(PassTypeDAO.class);
        mockGymPassDao = mock(GymPassDAO.class);
        mockPaymentDao = mock(PaymentDAO.class);
        mockGroupClassDao = mock(GroupClassDAO.class);
        mockReservationDao = mock(ReservationDAO.class);
        mockUserDao = mock(GymUserDAO.class);
        mockClubDao = mock(ClubDAO.class);
        mockMessageDao = mock(MessageDAO.class);

        handler = new ReceptionistCommandHandler(mockPassTypeDao, mockGymPassDao, mockPaymentDao, 
                                                 mockGroupClassDao, mockReservationDao, mockUserDao, 
                                                 mockClubDao, mockMessageDao);
        sw = new StringWriter();
        pw = new PrintWriter(sw);
    }

    @Test
    public void testSearchClient_Found() {
        GymUser user = new GymUser(1, "12345678901", "Jan", "Kowalski", "jan@test.pl", "hash", "CLIENT", "ACTIVE");
        when(mockUserDao.findByEmail("jan@test.pl")).thenReturn(Optional.of(user));
        
        String[] tokens = {"SEARCH_CLIENT", "jan@test.pl"};
        handler.handle(tokens[0], tokens, pw);
        pw.flush();
        
        assertTrue(sw.toString().startsWith("CLIENT_FOUND;1;Jan;Kowalski;jan@test.pl"));
    }

    @Test
    public void testVerifyEntry_Granted() {
        GymPass pass = new GymPass(1, 1, 1, BigDecimal.TEN, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(10), "ACTIVE");
        when(mockGymPassDao.findActiveByUserId(1)).thenReturn(Collections.singletonList(pass));
        
        String[] tokens = {"VERIFY_ENTRY", "1"};
        handler.handle(tokens[0], tokens, pw);
        pw.flush();
        
        assertTrue(sw.toString().contains("VERIFY_GRANTED"));
    }

    @Test
    public void testVerifyEntry_Denied() {
        when(mockGymPassDao.findActiveByUserId(1)).thenReturn(Collections.emptyList());
        
        String[] tokens = {"VERIFY_ENTRY", "1"};
        handler.handle(tokens[0], tokens, pw);
        pw.flush();
        
        assertTrue(sw.toString().contains("VERIFY_DENIED"));
    }
}