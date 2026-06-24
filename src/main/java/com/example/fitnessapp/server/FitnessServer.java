package com.example.fitnessapp.server;

import com.example.fitnessapp.database.DatabaseInitializer;
import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.model.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class FitnessServer {
    private static final int PORT = 5000;

    // Wszystkie obiekty dostępu do danych (DAO)
    private static final GymUserDAO userDao = new GymUserDaoJdbc();
    private static final EquipmentDAO equipmentDao = new EquipmentDaoJdbc();
    private static final ClubDAO clubDao = new ClubDaoJdbc();
    private static final TrainingPlanDAO trainingPlanDao = new TrainingPlanDaoJdbc();
    private static final ExerciseDictDAO exerciseDictDao = new ExerciseDictDaoJdbc();
    private static final PlanItemDAO planItemDao = new PlanItemDaoJdbc();
    private static final PassTypeDAO passTypeDao = new PassTypeDaoJdbc();
    private static final GymPassDAO gymPassDao = new GymPassDaoJdbc();
    private static final PaymentDAO paymentDao = new PaymentDaoJdbc();
    private static final GroupClassDAO groupClassDao = new GroupClassDaoJdbc();
    private static final ReservationDAO reservationDao = new ReservationDAOJdbc();
    private static final MessageDAO messageDao = new MessageDaoJdbc();
    private static final ComplaintDAO complaintDao = new ComplaintDaoJdbc();

    
    // Obiekty dla Managera
    private static final PromoCampaignDAO promoCampaignDao = new PromoCampaignDaoJdbc();
    private static final DiscountDAO discountDao = new DiscountDaoJdbc();
    private static final CampaignPassDAO campaignPassDao = new CampaignPassDaoJdbc();
    
    // Handlery poleceń
    private static final EquipmentCommandHandler equipmentHandler = new EquipmentCommandHandler(equipmentDao);
    private static final ClubCommandHandler clubHandler = new ClubCommandHandler(clubDao);
    private static final TrainerCommandHandler trainerHandler = new TrainerCommandHandler(exerciseDictDao, trainingPlanDao, planItemDao, userDao, messageDao);
    private static final ManagerCommandHandler managerHandler = new ManagerCommandHandler(promoCampaignDao, paymentDao, userDao, discountDao, campaignPassDao, passTypeDao);
    private static final ReceptionistCommandHandler receptionistHandler = new ReceptionistCommandHandler(
            passTypeDao, gymPassDao, paymentDao, groupClassDao, reservationDao, userDao, clubDao, messageDao
    );
    public static void main(String[] args) {
        //DatabaseInitializer.init();
        System.out.println("Baza danych zainicjalizowana.");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer Fitness wystartował na porcie: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowy klient podłączony: " + clientSocket.getInetAddress());
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas uruchamiania serwera na porcie " + PORT);
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Otrzymano od klienta: " + inputLine);
                String[] tokens = inputLine.split(";");
                String command = tokens[0];

                if ("LOGIN".equals(command)) {
                    Optional<GymUser> u = userDao.findByEmail(tokens[1]);
                    if (u.isPresent() && u.get().getPasswordHash().equals(tokens[2])) {
                        out.println("LOGIN_OK;" + u.get().getFirstName() + ";" + u.get().getRole() + ";" + u.get().getId());
                    } else {
                        out.println("LOGIN_ERROR;Nieprawidlowy email lub haslo");
                    }
                    continue; // Zatrzymaj sprawdzanie reszty i wróć na początek
                }
                
                if ("REGISTER".equals(command)) {
                    try {
                        GymUser user = new GymUser(0, tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], "ACTIVE");
                        userDao.save(user);
                        out.println("REGISTER_OK;Konto zostało pomyślnie utworzone");
                    } catch (Exception e) {
                        out.println("REGISTER_ERROR;Błąd tworzenia konta: " + e.getMessage());
                    }
                    continue; // Zatrzymaj sprawdzanie reszty
                }


                
                // Przekazanie do odpowiednich handlerów
                if (equipmentHandler.handle(command, tokens, out)) continue;
                if (clubHandler.handle(command, tokens, out)) continue;
                if (trainerHandler.handle(command, tokens, out)) continue;
                if (receptionistHandler.handle(command, tokens, out)) continue;
                
                // Nasz nowy handler Managera
                if (managerHandler.handle(command, tokens, out)) continue;

                if ("GET_COMPLAINTS".equals(command)) {
                    List<Complaint> list = complaintDao.findAll();

                    StringBuilder sb = new StringBuilder("COMPLAINTS_OK");

                    for (Complaint c : list) {
                        sb.append(";")
                                .append(c.getId())
                                .append(" | Autor: ")
                                .append(c.getAuthorId())
                                .append(" | ")
                                .append(c.getDescription())
                                .append(" | ")
                                .append(c.getStatus());
                    }

                    out.println(sb.toString());
                    continue;
                }

                if ("UPDATE_COMPLAINT_STATUS".equals(command)) {

                    try {

                        int complaintId = Integer.parseInt(tokens[1]);
                        String newStatus = tokens[2];

                        Optional<Complaint> complaint = complaintDao.findById(complaintId);

                        if (complaint.isPresent()) {

                            Complaint c = complaint.get();

                            c.setStatus(newStatus);

                            complaintDao.update(c);

                            out.println("UPDATE_OK;Status został zmieniony");

                        } else {

                            out.println("UPDATE_ERROR;Nie znaleziono reklamacji");

                        }

                    } catch (Exception e) {

                        out.println("UPDATE_ERROR;" + e.getMessage());

                    }

                    continue;
                }

                if ("GET_USERS".equals(command)) {

                    List<GymUser> list = userDao.findAll();

                    StringBuilder sb = new StringBuilder("USERS_OK");

                    for (GymUser u : list) {

                        sb.append(";")
                                .append("ID: ").append(u.getId())
                                .append(" | ")
                                .append(u.getFirstName()).append(" ")
                                .append(u.getLastName())
                                .append(" | ")
                                .append(u.getEmail())
                                .append(" | ")
                                .append(u.getRole())
                                .append(" | ")
                                .append(u.getStatus());

                    }

                    out.println(sb.toString());

                    continue;
                }
                if ("CHANGE_ROLE".equals(command)) {

                    try {

                        int id = Integer.parseInt(tokens[1]);

                        String newRole = tokens[2];

                        Optional<GymUser> user = userDao.findById(id);

                        if (user.isPresent()) {

                            GymUser u = user.get();

                            u.setRole(newRole);

                            userDao.update(u);

                            out.println("Rola została zmieniona.");

                        } else {

                            out.println("Nie znaleziono użytkownika.");

                        }

                    } catch (Exception e) {

                        out.println("Błąd: " + e.getMessage());

                    }

                    continue;
                }

                if ("CHANGE_STATUS".equals(command)) {

                    try {

                        int id = Integer.parseInt(tokens[1]);

                        String newStatus = tokens[2];

                        Optional<GymUser> user = userDao.findById(id);

                        if (user.isPresent()) {

                            GymUser u = user.get();

                            u.setStatus(newStatus);

                            userDao.update(u);

                            out.println("Status został zmieniony.");

                        } else {

                            out.println("Nie znaleziono użytkownika.");

                        }

                    } catch (Exception e) {

                        out.println("Błąd: " + e.getMessage());

                    }

                    continue;
                }

                if ("GET_EXERCISES".equals(command)) {
                    List<ExerciseDict> list = exerciseDictDao.findAll();
                    StringBuilder sb = new StringBuilder("EXERCISES_OK");
                    for (ExerciseDict e : list) {
                        sb.append(";").append(e.getId()).append(",")
                                .append(e.getName()).append(",")
                                .append(e.getMuscleGroup() != null ? e.getMuscleGroup() : "").append(",")
                                .append(e.getDescription() != null ? e.getDescription() : "");
                    }
                    out.println(sb.toString());
                    continue;
                }
                
                if ("GET_PLANS".equals(command)) {
                    int userId = Integer.parseInt(tokens[1]);
                    List<TrainingPlan> list = trainingPlanDao.findByUserId(userId);
                    StringBuilder sb = new StringBuilder("PLANS_OK");
                    for (TrainingPlan p : list) {
                        sb.append(";").append(p.getId()).append(",").append(p.getGoal());
                    }
                    out.println(sb.toString());
                    continue;
                }
                
                // Jeśli kod dotarł tutaj, to znaczy że ŻADEN z powyższych warunków nie zadziałał
                out.println("UNKNOWN_COMMAND;Serwer nie rozpoznaje polecenia");
            }
        } catch (IOException e) {
            System.out.println("Połączenie z klientem przerwane.");
        }
    }
}