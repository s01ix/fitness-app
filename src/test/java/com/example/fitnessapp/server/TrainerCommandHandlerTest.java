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

        // przygotowanie do przechwytywania odpowiedzi serwera
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    // dodawanie cwiczen
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

    // test gdy zabraknie parametrow w komendzie
    @Test
    public void testHandleAddExercise_NotEnoughTokens() {
        String[] tokens = {"ADD_EXERCISE", "Nazwa"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Za mało danych"));
    }

    // test gdy nazwa cwiczenia jest pusta (same spacje)
    @Test
    public void testHandleAddExercise_EmptyName() {
        String[] tokens = {"ADD_EXERCISE", "  ", "Nogi", "Opis", "Sprzęt"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Nazwa jest wymagana"));
    }

    // symuluje blad bazy danych podczas zapisu
    @Test
    public void testHandleAddExercise_DatabaseException() {
        doThrow(new RuntimeException("DB Error")).when(mockExerciseDao).save(any());
        String[] tokens = {"ADD_EXERCISE", "Przysiad", "Nogi", "Opis", "Sztanga"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("ADD_EXERCISE_ERROR"));
    }

    // sprawdzamy czy zwraca liste dwoch uzytkownikow
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
    public void testHandleGetClients_EmptyList() {
        when(mockUserDao.findAll()).thenReturn(Collections.emptyList());

        String[] tokens = {"GET_CLIENTS"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();

        assertEquals("CLIENTS_OK", stringWriter.toString().trim());
    }

    // symuluje blad podczas pobierania z bazy
    @Test
    public void testHandleGetClients_DatabaseException() {
        when(mockUserDao.findAll()).thenThrow(new RuntimeException("DB Error"));
        String[] tokens = {"GET_CLIENTS"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CLIENTS_ERROR"));
    }

    // test poprawnego utworzenia planu dla klienta
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

    // test gdy nazwa planu jest pusta
    @Test
    public void testHandleCreatePlan_MissingName() {
        String[] tokens = {"CREATE_PLAN", "10", "5", "   "};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().trim().startsWith("CREATE_PLAN_ERROR"));
        verify(mockPlanDao, never()).save(any(TrainingPlan.class));
    }

    // test gdy brakuje parametrow
    @Test
    public void testHandleCreatePlan_NotEnoughTokens() {
        String[] tokens = {"CREATE_PLAN", "10"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Za mało danych"));
    }

    // test gdy zamiast liczby dostaniemy tekst
    @Test
    public void testHandleCreatePlan_InvalidNumberFormat() {
        String[] tokens = {"CREATE_PLAN", "abc", "xyz", "Plan"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().startsWith("CREATE_PLAN_ERROR"));
    }

    // symuluje blad podczas zapisu do bazy
    @Test
    public void testHandleCreatePlan_DatabaseException() {
        doThrow(new RuntimeException("DB Error")).when(mockPlanDao).save(any());
        String[] tokens = {"CREATE_PLAN", "10", "5", "Plan"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CREATE_PLAN_ERROR"));
    }

    // ==================================================
    // ADD_PLAN_ITEM - dodawanie cwiczenia do planu
    // ==================================================

    // test dodania cwiczenia (4 serie po 12 powtorzen) do planu
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

    // test gdy zabraknie parametrow
    @Test
    public void testHandleAddPlanItem_NotEnoughTokens() {
        String[] tokens = {"ADD_PLAN_ITEM", "1", "2"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Za mało danych"));
    }

    // test gdy serie lub powtorzenia to nie liczby
    @Test
    public void testHandleAddPlanItem_InvalidNumbers() {
        String[] tokens = {"ADD_PLAN_ITEM", "1", "2", "abc", "xyz"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("ADD_PLAN_ITEM_ERROR"));
    }

    // symuluje blad bazy podczas zapisu
    @Test
    public void testHandleAddPlanItem_DatabaseException() {
        doThrow(new RuntimeException("DB Error")).when(mockPlanItemDao).save(any());
        String[] tokens = {"ADD_PLAN_ITEM", "1", "2", "4", "12"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("ADD_PLAN_ITEM_ERROR"));
    }

    // test pobierania listy planow dla danego klienta
    @Test
    public void testHandleGetClientPlans() {
        TrainingPlan plan = new TrainingPlan(1, 5, 10, "Masa", LocalDate.now());
        when(mockPlanDao.findByUserId(5)).thenReturn(Collections.singletonList(plan));

        String[] tokens = {"GET_CLIENT_PLANS", "5"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();

        assertEquals("CLIENT_PLANS_OK;1,Masa", stringWriter.toString().trim());
    }

    // test gdy klient nie ma jeszcze zadnych planow
    @Test
    public void testHandleGetClientPlans_EmptyList() {
        when(mockPlanDao.findByUserId(5)).thenReturn(Collections.emptyList());
        String[] tokens = {"GET_CLIENT_PLANS", "5"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertEquals("CLIENT_PLANS_OK", stringWriter.toString().trim());
    }

    // test gdy id klienta to nie liczba
    @Test
    public void testHandleGetClientPlans_InvalidClientId() {
        String[] tokens = {"GET_CLIENT_PLANS", "abc"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CLIENT_PLANS_ERROR"));
    }

    // symuluje blad bazy
    @Test
    public void testHandleGetClientPlans_DatabaseException() {
        when(mockPlanDao.findByUserId(anyInt())).thenThrow(new RuntimeException("DB Error"));
        String[] tokens = {"GET_CLIENT_PLANS", "5"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CLIENT_PLANS_ERROR"));
    }

    // test pobierania listy cwiczen dla konkretnego planu
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

    // test gdy cwiczenie zostalo usuniete ze slownika ale jest w planie
    @Test
    public void testHandleGetPlanItems_ExerciseNotFound() {
        PlanItem item = new PlanItem(1, 100, 999, 3, 10);
        when(mockPlanItemDao.findByPlanId(100)).thenReturn(Collections.singletonList(item));
        when(mockExerciseDao.findById(999)).thenReturn(Optional.empty());
        String[] tokens = {"GET_PLAN_ITEMS", "100"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Nieznane"));
    }

    // test gdy plan nie ma jeszcze zadnych cwiczen
    @Test
    public void testHandleGetPlanItems_EmptyPlan() {
        when(mockPlanItemDao.findByPlanId(100)).thenReturn(Collections.emptyList());
        String[] tokens = {"GET_PLAN_ITEMS", "100"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertEquals("PLAN_ITEMS_OK", stringWriter.toString().trim());
    }

    // symuluje blad bazy
    @Test
    public void testHandleGetPlanItems_DatabaseException() {
        when(mockPlanItemDao.findByPlanId(anyInt())).thenThrow(new RuntimeException("DB Error"));
        String[] tokens = {"GET_PLAN_ITEMS", "100"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("PLAN_ITEMS_ERROR"));
    }

    // test zmiany nazwy istniejacego planu
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

    // test gdy plan o podanym id nie istnieje
    @Test
    public void testHandleUpdatePlan_PlanNotFound() {
        when(mockPlanDao.findById(999)).thenReturn(Optional.empty());
        String[] tokens = {"UPDATE_PLAN", "999", "Nowa nazwa"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("UPDATE_PLAN_ERROR"));
        assertTrue(stringWriter.toString().contains("Nie znaleziono planu"));
    }

    // test gdy id planu to nie liczba
    @Test
    public void testHandleUpdatePlan_InvalidId() {
        String[] tokens = {"UPDATE_PLAN", "abc", "Nowa nazwa"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("UPDATE_PLAN_ERROR"));
    }

    // symuluje blad bazy podczas aktualizacji
    @Test
    public void testHandleUpdatePlan_DatabaseException() {
        TrainingPlan plan = new TrainingPlan(1, 5, 10, "Stara", LocalDate.now());
        when(mockPlanDao.findById(1)).thenReturn(Optional.of(plan));
        doThrow(new RuntimeException("DB Error")).when(mockPlanDao).update(any());
        String[] tokens = {"UPDATE_PLAN", "1", "Nowa"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("UPDATE_PLAN_ERROR"));
    }

    // test czyszczenia calego planu z cwiczen
    @Test
    public void testHandleClearPlanItems() {
        String[] tokens = {"CLEAR_PLAN_ITEMS", "100"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().trim().startsWith("CLEAR_PLAN_ITEMS_OK"));
        verify(mockPlanItemDao, times(1)).deleteByPlanId(100);
    }

    // test gdy id planu to nie liczba
    @Test
    public void testHandleClearPlanItems_InvalidId() {
        String[] tokens = {"CLEAR_PLAN_ITEMS", "abc"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CLEAR_PLAN_ITEMS_ERROR"));
    }

    // symuluje blad podczas usuwania
    @Test
    public void testHandleClearPlanItems_DatabaseException() {
        doThrow(new RuntimeException("DB Error")).when(mockPlanItemDao).deleteByPlanId(anyInt());
        String[] tokens = {"CLEAR_PLAN_ITEMS", "100"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CLEAR_PLAN_ITEMS_ERROR"));
    }

    // test pobierania wiadomosci miedzy trenerem a klientem
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
        // handler zamienia srednik w tresci na przecinek zeby nie zepsuc protokolu
        assertTrue(response.contains("Super, zrobione!"));
    }

    // test gdy nie ma jeszcze zadnych wiadomosci
    @Test
    public void testHandleGetChatHistory_EmptyConversation() {
        when(mockMessageDao.findConversation(10, 5)).thenReturn(Collections.emptyList());
        String[] tokens = {"GET_CHAT_HISTORY", "10", "5"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertEquals("CHAT_HISTORY_OK", stringWriter.toString().trim());
    }

    // test gdy id to nie liczby
    @Test
    public void testHandleGetChatHistory_InvalidIds() {
        String[] tokens = {"GET_CHAT_HISTORY", "abc", "xyz"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CHAT_HISTORY_ERROR"));
    }

    // symuluje blad bazy
    @Test
    public void testHandleGetChatHistory_DatabaseException() {
        when(mockMessageDao.findConversation(anyInt(), anyInt())).thenThrow(new RuntimeException("DB Error"));
        String[] tokens = {"GET_CHAT_HISTORY", "10", "5"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("CHAT_HISTORY_ERROR"));
    }

    // test wysylania wiadomosci ze srednikiem w tresci (handler musi je skleic)
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

    // test normalnej wiadomosci bez srednikow
    @Test
    public void testHandleSendMessage_Simple() {
        String[] tokens = {"SEND_MESSAGE", "10", "5", "Witaj"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("SEND_MESSAGE_OK"));
    }

    // test gdy id to nie liczby
    @Test
    public void testHandleSendMessage_InvalidIds() {
        String[] tokens = {"SEND_MESSAGE", "abc", "xyz", "Tresc"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("SEND_MESSAGE_ERROR"));
    }

    // symuluje blad podczas zapisu wiadomosci
    @Test
    public void testHandleSendMessage_DatabaseException() {
        doThrow(new RuntimeException("DB Error")).when(mockMessageDao).save(any());
        String[] tokens = {"SEND_MESSAGE", "10", "5", "Test"};
        handler.handle(tokens[0], tokens, printWriter);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("SEND_MESSAGE_ERROR"));
    }


    @Test
    public void testUnknownCommand() {
        String[] tokens = {"NIEZNANA_KOMENDA", "xyz"};
        boolean result = handler.handle(tokens[0], tokens, printWriter);
        assertFalse(result);
    }
}