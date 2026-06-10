package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.model.*;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

public class TrainerCommandHandler {

    private final ExerciseDictDAO exerciseDictDao;
    private final TrainingPlanDAO trainingPlanDao;
    private final PlanItemDAO planItemDao;
    private final GymUserDAO userDao;
    private final MessageDAO messageDao;

    public TrainerCommandHandler(ExerciseDictDAO exerciseDictDao,
                                 TrainingPlanDAO trainingPlanDao,
                                 PlanItemDAO planItemDao,
                                 GymUserDAO userDao,
                                 MessageDAO messageDao
            ) {
        this.exerciseDictDao = exerciseDictDao;
        this.trainingPlanDao = trainingPlanDao;
        this.planItemDao = planItemDao;
        this.userDao = userDao;
        this.messageDao = messageDao;
    }

    public boolean handle(String command, String[] tokens, PrintWriter out) {
        switch (command) {
            case "ADD_EXERCISE":
                return handleAddExercise(tokens, out);
            case "GET_CLIENTS":
                return handleGetClients(tokens, out);
            case "CREATE_PLAN":
                return handleCreatePlan(tokens, out);
            case "ADD_PLAN_ITEM":
                return handleAddPlanItem(tokens, out);
            case "GET_CLIENT_PLANS":
                return handleGetClientPlans(tokens, out);
            case "GET_PLAN_ITEMS":
                return handleGetPlanItems(tokens, out);
            case "UPDATE_PLAN":
                return handleUpdatePlan(tokens, out);
            case "CLEAR_PLAN_ITEMS":
                return handleClearPlanItems(tokens, out);
            case "GET_CHAT_HISTORY":
                return handleGetChatHistory(tokens, out);
            case "SEND_MESSAGE":
                return handleSendMessage(tokens, out);
            default:
                return false;        }
    }

    private boolean handleAddExercise(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 5) {
                out.println("ADD_EXERCISE_ERROR;Za mało danych");
                return true;
            }

            String name = tokens[1];
            String muscleGroup = tokens[2];
            String description = tokens[3];
            String requiredEquipment = tokens[4];

            if (name == null || name.trim().isEmpty()) {
                out.println("ADD_EXERCISE_ERROR;Nazwa jest wymagana");
                return true;
            }

            ExerciseDict exercise = new ExerciseDict(0, name, description, muscleGroup);
            exerciseDictDao.save(exercise);

            out.println("ADD_EXERCISE_OK;Ćwiczenie dodane;" + exercise.getId());
            return true;
        } catch (Exception e) {
            out.println("ADD_EXERCISE_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleGetClients(String[] tokens, PrintWriter out) {
        try {
            List<GymUser> users = userDao.findAll();
            StringBuilder sb = new StringBuilder("CLIENTS_OK");

            for (GymUser user : users) {
                sb.append(";")
                        .append(user.getId()).append(",")
                        .append(user.getFirstName()).append(",")
                        .append(user.getLastName()).append(",")
                        .append(user.getEmail());
            }

            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("CLIENTS_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleCreatePlan(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 4) {
                out.println("CREATE_PLAN_ERROR;Za mało danych");
                return true;
            }

            int trainerId = Integer.parseInt(tokens[1]);
            int clientId = Integer.parseInt(tokens[2]);
            String planName = tokens[3];

            if (planName == null || planName.trim().isEmpty()) {
                out.println("CREATE_PLAN_ERROR;Nazwa planu jest wymagana");
                return true;
            }

            TrainingPlan plan = new TrainingPlan(0, clientId, trainerId, planName, LocalDate.now());
            trainingPlanDao.save(plan);

            out.println("CREATE_PLAN_OK;" + plan.getId());
            return true;
        } catch (Exception e) {
            out.println("CREATE_PLAN_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleAddPlanItem(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 5) {
                out.println("ADD_PLAN_ITEM_ERROR;Za mało danych");
                return true;
            }

            int planId = Integer.parseInt(tokens[1]);
            int exerciseId = Integer.parseInt(tokens[2]);
            int sets = Integer.parseInt(tokens[3]);
            int reps = Integer.parseInt(tokens[4]);

            PlanItem item = new PlanItem(0, planId, exerciseId, sets, reps);
            planItemDao.save(item);

            out.println("ADD_PLAN_ITEM_OK;Ćwiczenie dodane do planu");
            return true;
        } catch (Exception e) {
            out.println("ADD_PLAN_ITEM_ERROR;Błąd: " + e.getMessage());
            return true;
        }

    }
    private boolean handleGetClientPlans(String[] tokens, PrintWriter out) {
        try {
            int clientId = Integer.parseInt(tokens[1]);
            List<TrainingPlan> plans = trainingPlanDao.findByUserId(clientId);
            StringBuilder sb = new StringBuilder("CLIENT_PLANS_OK");
            for (TrainingPlan plan : plans) {
                sb.append(";").append(plan.getId()).append(",").append(plan.getGoal());
            }
            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("CLIENT_PLANS_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleGetPlanItems(String[] tokens, PrintWriter out) {
        try {
            int planId = Integer.parseInt(tokens[1]);
            List<PlanItem> items = planItemDao.findByPlanId(planId);
            StringBuilder sb = new StringBuilder("PLAN_ITEMS_OK");
            for (PlanItem item : items) {
                ExerciseDict ex = exerciseDictDao.findById(item.getExerciseId()).orElse(null);
                String exName = (ex != null) ? ex.getName() : "Nieznane";

                sb.append(";").append(item.getExerciseId()).append(",")
                        .append(exName).append(",")
                        .append(item.getSets()).append(",")
                        .append(item.getReps());
            }
            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("PLAN_ITEMS_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleUpdatePlan(String[] tokens, PrintWriter out) {
        try {
            int planId = Integer.parseInt(tokens[1]);
            String newName = tokens[2];

            TrainingPlan plan = trainingPlanDao.findById(planId).orElseThrow(() -> new Exception("Nie znaleziono planu"));
            plan.setGoal(newName);
            trainingPlanDao.update(plan);

            out.println("UPDATE_PLAN_OK;" + planId);
            return true;
        } catch (Exception e) {
            out.println("UPDATE_PLAN_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleClearPlanItems(String[] tokens, PrintWriter out) {
        try {
            int planId = Integer.parseInt(tokens[1]);
            planItemDao.deleteByPlanId(planId);
            out.println("CLEAR_PLAN_ITEMS_OK");
            return true;
        } catch (Exception e) {
            out.println("CLEAR_PLAN_ITEMS_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleGetChatHistory(String[] tokens, PrintWriter out) {
        try {
            int trainerId = Integer.parseInt(tokens[1]);
            int clientId = Integer.parseInt(tokens[2]);

            List<Message> messages = messageDao.findConversation(trainerId, clientId);
            StringBuilder sb = new StringBuilder("CHAT_HISTORY_OK");

            for (Message m : messages) {
                // Formatujemy bezpiecznie treść, by średniki z czatu nie psuły parsowania na kliencie
                String safeContent = m.getContent().replace(";", ",");
                sb.append(";").append(m.getSenderId()).append("|")
                        .append(safeContent).append("|")
                        .append(m.getSentAt() != null ? m.getSentAt().toString() : "");
            }
            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("CHAT_HISTORY_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }

    private boolean handleSendMessage(String[] tokens, PrintWriter out) {
        try {
            int senderId = Integer.parseInt(tokens[1]);
            int receiverId = Integer.parseInt(tokens[2]);

            // Sklejamy treść wiadomości, jeśli klient użył średników (rozbite przez split na serwerze)
            StringBuilder contentBuilder = new StringBuilder();
            for (int i = 3; i < tokens.length; i++) {
                contentBuilder.append(tokens[i]).append(i == tokens.length - 1 ? "" : ";");
            }

            Message msg = new Message(0, senderId, receiverId, contentBuilder.toString(), null);
            messageDao.save(msg);

            out.println("SEND_MESSAGE_OK");
            return true;
        } catch (Exception e) {
            out.println("SEND_MESSAGE_ERROR;Błąd: " + e.getMessage());
            return true;
        }
    }
}