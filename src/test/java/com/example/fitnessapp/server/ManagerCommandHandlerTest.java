package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ManagerCommandHandlerTest {
    private ManagerCommandHandler handler;
    private PromoCampaignDAO mockPromoDao;
    private PaymentDAO mockPaymentDao;
    private GymUserDAO mockUserDao;
    private DiscountDAO mockDiscountDao;
    private CampaignPassDAO mockCPassDao;
    private PassTypeDAO mockPassTypeDao;
    
    private StringWriter sw;
    private PrintWriter pw;

    @BeforeEach
    public void setUp() {
        mockPromoDao = mock(PromoCampaignDAO.class);
        mockPaymentDao = mock(PaymentDAO.class);
        mockUserDao = mock(GymUserDAO.class);
        mockDiscountDao = mock(DiscountDAO.class);
        mockCPassDao = mock(CampaignPassDAO.class);
        mockPassTypeDao = mock(PassTypeDAO.class);

        handler = new ManagerCommandHandler(mockPromoDao, mockPaymentDao, mockUserDao, 
                                            mockDiscountDao, mockCPassDao, mockPassTypeDao);
        sw = new StringWriter();
        pw = new PrintWriter(sw);
    }

    @Test
    public void testAddCampaign_Success() {
        String[] tokens = {"ADD_CAMPAIGN", "Lato2026", "Studenci", "1000", "2026-06-01", "2026-08-01"};
        boolean result = handler.handle(tokens[0], tokens, pw);
        pw.flush();
        
        assertTrue(result);
        verify(mockPromoDao, times(1)).save(any(PromoCampaign.class));
        assertTrue(sw.toString().contains("ADD_CAMPAIGN_OK"));
    }

    @Test
    public void testGetStats_Success() {
        when(mockPaymentDao.findAll()).thenReturn(Collections.singletonList(new Payment(1, 1, new BigDecimal("100.00"), java.time.LocalDateTime.now(), "CARD")));
        when(mockUserDao.findAll()).thenReturn(Collections.singletonList(new GymUser()));
        
        String[] tokens = {"GET_MGR_STATS"};
        handler.handle(tokens[0], tokens, pw);
        pw.flush();
        
        assertTrue(sw.toString().contains("MGR_STATS_OK;100.00;1;1"));
    }

    @Test
    public void testDeleteDiscount_InvalidId() {
        String[] tokens = {"DELETE_DISCOUNT", "abc"}; // Niepoprawne ID
        handler.handle(tokens[0], tokens, pw);
        pw.flush();
        assertTrue(sw.toString().contains("DELETE_DISCOUNT_ERROR"));
    }
}