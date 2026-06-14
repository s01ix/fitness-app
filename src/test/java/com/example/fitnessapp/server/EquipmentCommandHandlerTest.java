package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.EquipmentDAO;
import com.example.fitnessapp.model.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EquipmentCommandHandlerTest {

    @Mock
    private EquipmentDAO equipmentDao;

    private EquipmentCommandHandler handler;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new EquipmentCommandHandler(equipmentDao);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    void testAddEquipmentSuccess() {
        String[] tokens = {"ADD_EQUIPMENT", "1", "Bieżnia", "SPRAWNY", "2024-01-15"};

        doAnswer(invocation -> {
            Equipment eq = invocation.getArgument(0);
            eq.setId(10);
            return null;
        }).when(equipmentDao).save(any(Equipment.class));

        boolean result = handler.handle("ADD_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_EQUIPMENT_OK"));
        assertTrue(output.contains("Sprzet dodany"));

        ArgumentCaptor<Equipment> captor = ArgumentCaptor.forClass(Equipment.class);
        verify(equipmentDao).save(captor.capture());

        Equipment saved = captor.getValue();
        assertEquals(1, saved.getClubId());
        assertEquals("Bieżnia", saved.getName());
        assertEquals("SPRAWNY", saved.getStatus());
        assertEquals(LocalDate.parse("2024-01-15"), saved.getLastInspectionDate());
    }

    @Test
    void testAddEquipmentInsufficientData() {
        String[] tokens = {"ADD_EQUIPMENT", "1", "Bieżnia"};

        boolean result = handler.handle("ADD_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_EQUIPMENT_ERROR"));
        assertTrue(output.contains("Za malo danych"));

        verify(equipmentDao, never()).save(any());
    }

    @Test
    void testAddEquipmentEmptyName() {
        String[] tokens = {"ADD_EQUIPMENT", "1", "", "SPRAWNY", "2024-01-15"};

        boolean result = handler.handle("ADD_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_EQUIPMENT_ERROR"));
        assertTrue(output.contains("Nazwa jest wymagana"));

        verify(equipmentDao, never()).save(any());
    }

    @Test
    void testAddEquipmentEmptyStatus() {
        String[] tokens = {"ADD_EQUIPMENT", "1", "Bieżnia", "  ", "2024-01-15"};

        boolean result = handler.handle("ADD_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_EQUIPMENT_ERROR"));
        assertTrue(output.contains("Status jest wymagany"));

        verify(equipmentDao, never()).save(any());
    }

    @Test
    void testAddEquipmentInvalidDateFormat() {
        String[] tokens = {"ADD_EQUIPMENT", "1", "Bieżnia", "SPRAWNY", "15-01-2024"};

        boolean result = handler.handle("ADD_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_EQUIPMENT_ERROR"));
        assertTrue(output.contains("Blad dodawania sprzetu"));

        verify(equipmentDao, never()).save(any());
    }

    @Test
    void testGetAllEquipment() {
        List<Equipment> equipmentList = Arrays.asList(
                new Equipment(1, 1, "Bieżnia", "SPRAWNY", LocalDate.parse("2024-01-15")),
                new Equipment(2, 1, "Rower", "USZKODZONY", LocalDate.parse("2024-02-20")),
                new Equipment(3, 2, "Orbitrek", "SPRAWNY", LocalDate.parse("2024-03-10"))
        );

        when(equipmentDao.findAll()).thenReturn(equipmentList);

        String[] tokens = {"GET_EQUIPMENT"};
        boolean result = handler.handle("GET_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("EQUIPMENT_OK"));
        assertTrue(output.contains("Bieżnia"));
        assertTrue(output.contains("Rower"));
        assertTrue(output.contains("Orbitrek"));
        assertTrue(output.contains("SPRAWNY"));
        assertTrue(output.contains("USZKODZONY"));

        verify(equipmentDao).findAll();
        verify(equipmentDao, never()).findByClubId(anyInt());
    }

    @Test
    void testGetEquipmentByClubId() {
        List<Equipment> equipmentList = Arrays.asList(
                new Equipment(1, 1, "Bieżnia", "SPRAWNY", LocalDate.parse("2024-01-15")),
                new Equipment(2, 1, "Rower", "SPRAWNY", LocalDate.parse("2024-02-20"))
        );

        when(equipmentDao.findByClubId(1)).thenReturn(equipmentList);

        String[] tokens = {"GET_EQUIPMENT", "1"};
        boolean result = handler.handle("GET_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("EQUIPMENT_OK"));
        assertTrue(output.contains("Bieżnia"));
        assertTrue(output.contains("Rower"));

        verify(equipmentDao).findByClubId(1);
        verify(equipmentDao, never()).findAll();
    }

    @Test
    void testUnhandledCommand() {
        String[] tokens = {"UNKNOWN_COMMAND"};

        boolean result = handler.handle("UNKNOWN_COMMAND", tokens, printWriter);

        assertFalse(result);
        verify(equipmentDao, never()).save(any());
        verify(equipmentDao, never()).findAll();
    }

    @Test
    void testAddEquipmentDatabaseException() {
        String[] tokens = {"ADD_EQUIPMENT", "1", "Bieżnia", "SPRAWNY", "2024-01-15"};

        doThrow(new RuntimeException("Database connection error"))
                .when(equipmentDao).save(any(Equipment.class));

        boolean result = handler.handle("ADD_EQUIPMENT", tokens, printWriter);

        assertTrue(result);
        String output = stringWriter.toString();
        assertTrue(output.contains("ADD_EQUIPMENT_ERROR"));
        assertTrue(output.contains("Database connection error"));
    }
}