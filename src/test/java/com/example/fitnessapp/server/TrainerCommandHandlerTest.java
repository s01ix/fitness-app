package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainerCommandHandlerTest {
    private TrainerCommandHandler handler;
    private ExerciseDictDAO mockExerciseDao;
    private TrainingPlanDAO mockPlanDao;
    private PlanItemDAO mockPlanItemDao;
    private GymUserDAO mockUserDao;
    private MessageDAO mockMessageDao;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() {
        mockExerciseDao = mock(ExerciseDictDAO.class);
        mockPlanDao = mock(TrainingPlanDAO.class);
        mockPlanItemDao = mock(PlanItemDAO.class);
        mockUserDao = mock(GymUserDAO.class);
        mockMessageDao = mock(MessageDAO.class);

        handler = new TrainerCommandHandler(
                mockExerciseDao, mockPlanDao, mockPlanItemDao, mockUserDao, mockMessageDao
        );

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    public void testHandleAddExercise_Success() {
        String[] tokens = {"ADD_EXERCISE", "Przysiad", "Nogi", "Zejście w dół", "Sztanga"};
        boolean result = handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(result);
        assertTrue(stringWriter.toString().trim().startsWith("ADD_EXERCISE_OK"));
        ArgumentCaptor<ExerciseDict> captor = ArgumentCaptor.forClass(ExerciseDict.class);
        verify(mockExerciseDao, times(1)).save(captor.capture());
        assertEquals("Przysiad", captor.getValue().getName());
        assertEquals("Nogi", captor.getValue().getMuscleGroup());
    }

    @Test
    public void testHandleAddExercise_EmptyName() {
        String[] tokens = {"ADD_EXERCISE", "  ", "Nogi", "Opis", "Sprzęt"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Nazwa jest wymagana"));
    }

    @Test
    public void testHandleGetClients_Success() {
        GymUser user1 = new GymUser(1, "123", "Jan", "Kowalski", "jan@test.pl", "hash", "CLIENT", "ACTIVE");
        GymUser user2 = new GymUser(2, "321", "Anna", "Nowak", "anna@test.pl", "hash", "CLIENT", "ACTIVE");
        when(mockUserDao.findAll()).thenReturn(Arrays.asList(user1, user2));
        String[] tokens = {"GET_CLIENTS"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        String response = stringWriter.toString().trim();
        assertTrue(response.startsWith("CLIENTS_OK"));
        assertTrue(response.contains(";1,Jan,Kowalski,jan@test.pl"));
        assertTrue(response.contains(";2,Anna,Nowak,anna@test.pl"));
    }

    @Test
    public void testHandleCreatePlan_Success() {
        String[] tokens = {"CREATE_PLAN", "10", "5", "Plan na rzeźbę"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().trim().startsWith("CREATE_PLAN_OK"));
        ArgumentCaptor<TrainingPlan> captor = ArgumentCaptor.forClass(TrainingPlan.class);
        verify(mockPlanDao, times(1)).save(captor.capture());
        assertEquals(10, captor.getValue().getTrainerId());
        assertEquals(5, captor.getValue().getUserId());
        assertEquals("Plan na rzeźbę", captor.getValue().getGoal());
    }

    @Test
    public void testHandleAddPlanItem_Success() {
        String[] tokens = {"ADD_PLAN_ITEM", "1", "2", "4", "12"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().trim().startsWith("ADD_PLAN_ITEM_OK"));
        ArgumentCaptor<PlanItem> captor = ArgumentCaptor.forClass(PlanItem.class);
        verify(mockPlanItemDao, times(1)).save(captor.capture());
        assertEquals(1, captor.getValue().getPlanId());
        assertEquals(2, captor.getValue().getExerciseId());
        assertEquals(4, captor.getValue().getSets());
        assertEquals(12, captor.getValue().getReps());
    }

    @Test
    public void testHandleGetClientPlans() {
        TrainingPlan plan = new TrainingPlan(1, 5, 10, "Masa", LocalDate.now());
        when(mockPlanDao.findByUserId(5)).thenReturn(Collections.singletonList(plan));
        String[] tokens = {"GET_CLIENT_PLANS", "5"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertEquals("CLIENT_PLANS_OK;1,Masa", stringWriter.toString().trim());
    }

    @Test
    public void testHandleGetPlanItems() {
        PlanItem item = new PlanItem(1, 100, 2, 3, 10);
        ExerciseDict ex = new ExerciseDict(2, "Wyciskanie", "Opis", "Klatka");
        when(mockPlanItemDao.findByPlanId(100)).thenReturn(Collections.singletonList(item));
        when(mockExerciseDao.findById(2)).thenReturn(Optional.of(ex));
        String[] tokens = {"GET_PLAN_ITEMS", "100"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertEquals("PLAN_ITEMS_OK;2,Wyciskanie,3,10", stringWriter.toString().trim());
    }

    @Test
    public void testHandleUpdatePlan_Success() {
        TrainingPlan existingPlan = new TrainingPlan(1, 5, 10, "Stara Nazwa", LocalDate.now());
        when(mockPlanDao.findById(1)).thenReturn(Optional.of(existingPlan));
        String[] tokens = {"UPDATE_PLAN", "1", "Nowa Nazwa"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().trim().startsWith("UPDATE_PLAN_OK"));
        assertEquals("Nowa Nazwa", existingPlan.getGoal());
        verify(mockPlanDao, times(1)).update(existingPlan);
    }

    @Test
    public void testHandleClearPlanItems() {
        String[] tokens = {"CLEAR_PLAN_ITEMS", "100"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().trim().startsWith("CLEAR_PLAN_ITEMS_OK"));
        verify(mockPlanItemDao, times(1)).deleteByPlanId(100);
    }

    @Test
    public void testHandleGetChatHistory() {
        LocalDateTime now = LocalDateTime.now();
        Message msg1 = new Message(1, 10, 5, "Czesc, jak trening?", now);
        Message msg2 = new Message(2, 5, 10, "Super; zrobione!", now);
        when(mockMessageDao.findConversation(10, 5)).thenReturn(Arrays.asList(msg1, msg2));
        String[] tokens = {"GET_CHAT_HISTORY", "10", "5"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        String response = stringWriter.toString().trim();
        assertTrue(response.startsWith("CHAT_HISTORY_OK"));
        assertTrue(response.contains("Super, zrobione!"));
    }

    @Test
    public void testHandleSendMessage_WithSemicolons() {
        String[] tokens = {"SEND_MESSAGE", "10", "5", "Czesc", " jak tam?"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().trim().startsWith("SEND_MESSAGE_OK"));
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(mockMessageDao, times(1)).save(captor.capture());
        assertEquals(10, captor.getValue().getSenderId());
        assertEquals(5, captor.getValue().getReceiverId());
        assertEquals("Czesc; jak tam?", captor.getValue().getContent());
    }

    @Test
    public void testHandleCreatePlan_DatabaseException() {
        doThrow(new RuntimeException("DB Error")).when(mockPlanDao).save(any());
        String[] tokens = {"CREATE_PLAN", "10", "5", "Plan"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CREATE_PLAN_ERROR"));
    }

    @Test
    public void testUnknownCommand() {
        String[] tokens = {"NIEZNANA_KOMENDA", "xyz"};
        boolean result = handler.handle(tokens[0], tokens, printWriter);
        assertFalse(result);
    }
}