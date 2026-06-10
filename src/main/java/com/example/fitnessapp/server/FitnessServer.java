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

    private static final EquipmentCommandHandler equipmentHandler = new EquipmentCommandHandler(equipmentDao);
    private static final ClubCommandHandler clubHandler = new ClubCommandHandler(clubDao);
    private static final TrainerCommandHandler trainerHandler = new TrainerCommandHandler(exerciseDictDao, trainingPlanDao, planItemDao, userDao, messageDao);
    private static final ReceptionistCommandHandler receptionistHandler = new ReceptionistCommandHandler(passTypeDao, gymPassDao, paymentDao, groupClassDao, reservationDao, userDao);
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
                }
                else if ("REGISTER".equals(command)) {
                    try {
                        GymUser user = new GymUser(0, tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], "ACTIVE");
                        userDao.save(user);
                        out.println("REGISTER_OK;Konto zostało pomyślnie utworzone");
                    } catch (Exception e) {
                        out.println("REGISTER_ERROR;Błąd tworzenia konta: " + e.getMessage());
                    }
                }
                else if (equipmentHandler.handle(command, tokens, out)) {
                    // handled by equipment handler
                }
                else if (clubHandler.handle(command, tokens, out)) {
                }
                else if (trainerHandler.handle(command, tokens, out)) {
                }
                else if(receptionistHandler.handle(command, tokens, out)){

                }

                else if ("GET_EXERCISES".equals(command)) {
                    List<ExerciseDict> list = exerciseDictDao.findAll();
                    StringBuilder sb = new StringBuilder("EXERCISES_OK");
                    for (ExerciseDict e : list) {
                        // Format: EXERCISES_OK;id,nazwa,grupaMięśniowa,opis
                        sb.append(";").append(e.getId()).append(",")
                                .append(e.getName()).append(",")
                                .append(e.getMuscleGroup() != null ? e.getMuscleGroup() : "").append(",")
                                .append(e.getDescription() != null ? e.getDescription() : "");
                    }
                    out.println(sb.toString());
                }
                else if ("GET_PLANS".equals(command)) {
                    int userId = Integer.parseInt(tokens[1]);
                    List<TrainingPlan> list = trainingPlanDao.findByUserId(userId);
                    StringBuilder sb = new StringBuilder("PLANS_OK");
                    for (TrainingPlan p : list) {
                        sb.append(";").append(p.getId()).append(",").append(p.getGoal());
                    }
                    out.println(sb.toString());
                }
                else {
                    out.println("UNKNOWN_COMMAND;Serwer nie rozpoznaje polecenia");
                }
            }
        } catch (IOException e) {
            System.out.println("Połączenie z klientem przerwane.");
        }
    }
}