package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import com.example.fitnessapp.model.ExerciseDict;
import com.example.fitnessapp.model.GymUser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class TrainerView extends VBox {
    private NetworkClient networkClient;
    private int currentUserId;

    private TabPane tabPane;

    private TableView<ExerciseDict> exercisesTable;
    private ObservableList<ExerciseDict> exercisesList = FXCollections.observableArrayList();
    private TextField exerciseName, exerciseMuscleGroup, exerciseDescription, exerciseEquipment;
    private Button addExerciseBtn, refreshExercisesBtn;

    private ComboBox<String> clientComboBox;
    private List<GymUser> clientsList = new ArrayList<>();
    private TextField planNameField;
    private TableView<ExerciseDict> availableExercisesTable;
    private ObservableList<ExerciseDict> availableExercisesList = FXCollections.observableArrayList();

    private ComboBox<String> selectedExerciseCombo;
    private TextField setsField, repsField;
    private Button addToPlanBtn;

    private TableView<PlanItemDisplay> currentPlanTable;
    private ObservableList<PlanItemDisplay> currentPlanItems = FXCollections.observableArrayList();
    private Button savePlanBtn;
    private ComboBox<String> existingPlansCombo;
    private int currentEditingPlanId = -1;
    private Button createNewPlanBtn;

    public TrainerView(NetworkClient networkClient, int currentUserId) {
        this.networkClient = networkClient;
        this.currentUserId = currentUserId;

        Label titleLabel = new Label("Panel Trenera");

        tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createExercisesTab(),
                createPlansTab()
        );

        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.getChildren().addAll(titleLabel, tabPane);

        loadExercises();
        loadClients();
    }

    private Tab createExercisesTab() {
        Tab tab = new Tab("Zarządzanie ćwiczeniami");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        exercisesTable = new TableView<>();
        exercisesTable.setItems(exercisesList);
        exercisesTable.setPrefHeight(250);

        TableColumn<ExerciseDict, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<ExerciseDict, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<ExerciseDict, String> muscleCol = new TableColumn<>("Grupa mięśniowa");
        muscleCol.setCellValueFactory(new PropertyValueFactory<>("muscleGroup"));
        muscleCol.setPrefWidth(150);

        TableColumn<ExerciseDict, String> descCol = new TableColumn<>("Opis");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        exercisesTable.getColumns().addAll(idCol, nameCol, muscleCol, descCol);

        refreshExercisesBtn = new Button("Odśwież listę");
        refreshExercisesBtn.setOnAction(e -> loadExercises());

        Label addLabel = new Label("Dodaj nowe ćwiczenie:");
        exerciseName = new TextField();
        exerciseName.setPromptText("Nazwa ćwiczenia");

        exerciseMuscleGroup = new TextField();
        exerciseMuscleGroup.setPromptText("Grupa mięśniowa (np. Klatka piersiowa)");

        exerciseDescription = new TextField();
        exerciseDescription.setPromptText("Opis techniki");

        exerciseEquipment = new TextField();
        exerciseEquipment.setPromptText("Wymagany sprzęt (np. Sztanga)");

        addExerciseBtn = new Button("Dodaj ćwiczenie");
        addExerciseBtn.setOnAction(e -> handleAddExercise());

        content.getChildren().addAll(
                new Label("Lista ćwiczeń:"),
                exercisesTable,
                refreshExercisesBtn,
                new Separator(),
                addLabel,
                exerciseName,
                exerciseMuscleGroup,
                exerciseDescription,
                exerciseEquipment,
                addExerciseBtn
        );

        tab.setContent(content);
        return tab;
    }

    private Tab createPlansTab() {
        Tab tab = new Tab("Tworzenie planów treningowych");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label clientLabel = new Label("1. Wybierz klienta:");

        clientComboBox = new ComboBox<>();
        clientComboBox.setPromptText("Wybierz użytkownika");
        clientComboBox.setPrefWidth(300);


        Button refreshClientsBtn = new Button("Odśwież listę");
        refreshClientsBtn.setOnAction(e -> loadClients());

        HBox clientBox = new HBox(10, clientComboBox, refreshClientsBtn);

/// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Label planSelectLabel = new Label("1b. Wybierz plan do edycji (lub utwórz nowy):");
        existingPlansCombo = new ComboBox<>();
        existingPlansCombo.setPromptText("Wybierz plan...");
        existingPlansCombo.setPrefWidth(200);

        createNewPlanBtn = new Button("Nowy plan");
        createNewPlanBtn.setOnAction(e -> resetPlanEditor());
        HBox planSelectBox = new HBox(10, existingPlansCombo, createNewPlanBtn);

        clientComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                int clientId = Integer.parseInt(newVal.split(" - ")[0]);
                loadClientPlans(clientId);
            }
        });
        existingPlansCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                currentEditingPlanId = Integer.parseInt(newVal.split(" - ")[0]);
                planNameField.setText(newVal.substring(newVal.indexOf(" - ") + 3));
                loadPlanItems(currentEditingPlanId);
            }
        });

        /// /////////////////////////////////////////////////////////////////////////////////






        Label planLabel = new Label("2. Podaj nazwę planu:");

        planNameField = new TextField();
        planNameField.setPromptText("Np. Plan na masę, Redukcja, FBW");
        planNameField.setPrefWidth(300);

        Label exercisesLabel = new Label("3. Dodaj ćwiczenia do planu:");

        selectedExerciseCombo = new ComboBox<>();
        selectedExerciseCombo.setPromptText("Wybierz ćwiczenie");
        selectedExerciseCombo.setPrefWidth(250);

        setsField = new TextField();
        setsField.setPromptText("Serie");
        setsField.setPrefWidth(80);

        repsField = new TextField();
        repsField.setPromptText("Powtórzenia");
        repsField.setPrefWidth(100);

        addToPlanBtn = new Button("Dodaj do planu");
        addToPlanBtn.setOnAction(e -> handleAddToPlan());

        HBox exerciseInputBox = new HBox(10, selectedExerciseCombo, setsField, repsField, addToPlanBtn);

        currentPlanTable = new TableView<>();
        currentPlanTable.setItems(currentPlanItems);
        currentPlanTable.setPrefHeight(200);

        TableColumn<PlanItemDisplay, String> planExNameCol = new TableColumn<>("Ćwiczenie");
        planExNameCol.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        planExNameCol.setPrefWidth(250);

        TableColumn<PlanItemDisplay, Integer> planSetsCol = new TableColumn<>("Serie");
        planSetsCol.setCellValueFactory(new PropertyValueFactory<>("sets"));
        planSetsCol.setPrefWidth(80);

        TableColumn<PlanItemDisplay, Integer> planRepsCol = new TableColumn<>("Powtórzenia");
        planRepsCol.setCellValueFactory(new PropertyValueFactory<>("reps"));
        planRepsCol.setPrefWidth(100);

        currentPlanTable.getColumns().addAll(planExNameCol, planSetsCol, planRepsCol);

        Button removePlanItemBtn = new Button("Usuń zaznaczone");
        removePlanItemBtn.setOnAction(e -> {
            PlanItemDisplay selected = currentPlanTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentPlanItems.remove(selected);
            }
        });

        savePlanBtn = new Button("Zapisz plan treningowy");
        savePlanBtn.setOnAction(e -> handleSavePlan());

        content.getChildren().addAll(
                clientLabel,
                clientBox,
                planSelectLabel,
                planSelectBox,
                new Separator(),
                planLabel,
                planNameField,
                new Separator(),
                exercisesLabel,
                exerciseInputBox,
                new Label("Aktualny plan:"),
                currentPlanTable,
                removePlanItemBtn,
                new Separator(),
                savePlanBtn
        );

        tab.setContent(content);
        return tab;
    }

    private void loadExercises() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_EXERCISES");
            Platform.runLater(() -> {
                exercisesList.clear();
                availableExercisesList.clear();
                selectedExerciseCombo.getItems().clear();

                if (resp != null && resp.startsWith("EXERCISES_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] parts = tokens[i].split(",", 4);
                        if (parts.length >= 2) {
                            int id = Integer.parseInt(parts[0]);
                            String name = parts[1];
                            String muscleGroup = parts.length > 2 ? parts[2] : "";
                            String description = parts.length > 3 ? parts[3] : "";

                            ExerciseDict ex = new ExerciseDict(id, name, description, muscleGroup);
                            exercisesList.add(ex);
                            availableExercisesList.add(ex);
                            selectedExerciseCombo.getItems().add(id + " - " + name);
                        }
                    }
                }
            });
        }).start();
    }

    private void handleAddExercise() {
        String name = exerciseName.getText().trim();
        String muscleGroup = exerciseMuscleGroup.getText().trim();
        String description = exerciseDescription.getText().trim();
        String equipment = exerciseEquipment.getText().trim();

        if (name.isEmpty()) {
            showAlert("Błąd", "Nazwa ćwiczenia jest wymagana", Alert.AlertType.ERROR);
            return;
        }

        new Thread(() -> {
            String req = String.format("ADD_EXERCISE;%s;%s;%s;%s", name, muscleGroup, description, equipment);
            String resp = networkClient.sendRequest(req);

            Platform.runLater(() -> {
                if (resp != null && resp.startsWith("ADD_EXERCISE_OK")) {
                    showAlert("Sukces", "Ćwiczenie zostało dodane", Alert.AlertType.INFORMATION);
                    exerciseName.clear();
                    exerciseMuscleGroup.clear();
                    exerciseDescription.clear();
                    exerciseEquipment.clear();
                    loadExercises();
                } else {
                    showAlert("Błąd", resp != null ? resp : "Nieznany błąd", Alert.AlertType.ERROR);
                }
            });
        }).start();
    }

    private void loadClients() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_CLIENTS");
            Platform.runLater(() -> {
                clientsList.clear();
                clientComboBox.getItems().clear();

                if (resp != null && resp.startsWith("CLIENTS_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] parts = tokens[i].split(",", 4);
                        if (parts.length >= 4) {
                            int id = Integer.parseInt(parts[0]);
                            String firstName = parts[1];
                            String lastName = parts[2];
                            String email = parts[3];

                            GymUser user = new GymUser(id, "", firstName, lastName, email, "", "CLIENT", "ACTIVE");
                            clientsList.add(user);
                            clientComboBox.getItems().add(id + " - " + firstName + " " + lastName + " (" + email + ")");
                        }
                    }
                }
            });
        }).start();
    }

    private void handleAddToPlan() {
        String selected = selectedExerciseCombo.getValue();
        if (selected == null || selected.isEmpty()) {
            showAlert("Błąd", "Wybierz ćwiczenie", Alert.AlertType.WARNING);
            return;
        }

        int exerciseId = Integer.parseInt(selected.split(" - ")[0]);
        String exerciseName = selected.substring(selected.indexOf(" - ") + 3);

        try {
            int sets = Integer.parseInt(setsField.getText().trim());
            int reps = Integer.parseInt(repsField.getText().trim());

            currentPlanItems.add(new PlanItemDisplay(exerciseId, exerciseName, sets, reps));
            setsField.clear();
            repsField.clear();
        } catch (NumberFormatException e) {
            showAlert("Błąd", "Serie i powtórzenia muszą być liczbami", Alert.AlertType.ERROR);
        }
    }
    private void loadClientPlans(int clientId) {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_CLIENT_PLANS;" + clientId);
            Platform.runLater(() -> {
                existingPlansCombo.getItems().clear();
                if (resp != null && resp.startsWith("CLIENT_PLANS_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] parts = tokens[i].split(",", 2);
                        if (parts.length == 2) {
                            existingPlansCombo.getItems().add(parts[0] + " - " + parts[1]);
                        }
                    }
                }
            });
        }).start();
    }

    private void loadPlanItems(int planId) {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_PLAN_ITEMS;" + planId);
            Platform.runLater(() -> {
                currentPlanItems.clear();
                if (resp != null && resp.startsWith("PLAN_ITEMS_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] parts = tokens[i].split(",", 4);
                        if (parts.length == 4) {
                            currentPlanItems.add(new PlanItemDisplay(
                                    Integer.parseInt(parts[0]),
                                    parts[1],
                                    Integer.parseInt(parts[2]),
                                    Integer.parseInt(parts[3])
                            ));
                        }
                    }
                }
            });
        }).start();
    }

    private void resetPlanEditor() {
        currentEditingPlanId = -1;
        existingPlansCombo.getSelectionModel().clearSelection();
        planNameField.clear();
        currentPlanItems.clear();
    }

    private void handleSavePlan() {
        String selectedClient = clientComboBox.getValue();
        String planName = planNameField.getText().trim();

        if (selectedClient == null || selectedClient.isEmpty()) {
            showAlert("Błąd", "Wybierz klienta", Alert.AlertType.WARNING);
            return;
        }
        if (planName.isEmpty()) {
            showAlert("Błąd", "Podaj nazwę planu", Alert.AlertType.WARNING);
            return;
        }
        if (currentPlanItems.isEmpty()) {
            showAlert("Błąd", "Dodaj przynajmniej jedno ćwiczenie do planu", Alert.AlertType.WARNING);
            return;
        }

        int clientId = Integer.parseInt(selectedClient.split(" - ")[0]);

        new Thread(() -> {
            int targetPlanId = currentEditingPlanId;

            if (targetPlanId == -1) {
                String createReq = String.format("CREATE_PLAN;%d;%d;%s", currentUserId, clientId, planName);
                String createResp = networkClient.sendRequest(createReq);
                if (createResp != null && createResp.startsWith("CREATE_PLAN_OK")) {
                    targetPlanId = Integer.parseInt(createResp.split(";")[1]);
                } else {
                    Platform.runLater(() -> showAlert("Błąd", "Nie udało się utworzyć planu", Alert.AlertType.ERROR));
                    return;
                }
            } else {
                networkClient.sendRequest(String.format("UPDATE_PLAN;%d;%s", targetPlanId, planName));
                networkClient.sendRequest("CLEAR_PLAN_ITEMS;" + targetPlanId);
            }

            boolean allSuccess = true;
            for (PlanItemDisplay item : currentPlanItems) {
                String itemReq = String.format("ADD_PLAN_ITEM;%d;%d;%d;%d",
                        targetPlanId, item.getExerciseId(), item.getSets(), item.getReps());
                String itemResp = networkClient.sendRequest(itemReq);

                if (itemResp == null || !itemResp.startsWith("ADD_PLAN_ITEM_OK")) {
                    allSuccess = false;
                    break;
                }
            }

            boolean finalSuccess = allSuccess;
            Platform.runLater(() -> {
                if (finalSuccess) {
                    showAlert("Sukces", "Plan treningowy został zapisany!", Alert.AlertType.INFORMATION);
                    resetPlanEditor();
                    loadClientPlans(clientId);
                } else {
                    showAlert("Błąd", "Nie udało się zapisać wszystkich ćwiczeń", Alert.AlertType.ERROR);
                }
            });
        }).start();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class PlanItemDisplay {
        private final int exerciseId;
        private final String exerciseName;
        private final int sets;
        private final int reps;

        public PlanItemDisplay(int exerciseId, String exerciseName, int sets, int reps) {
            this.exerciseId = exerciseId;
            this.exerciseName = exerciseName;
            this.sets = sets;
            this.reps = reps;
        }

        public int getExerciseId() { return exerciseId; }
        public String getExerciseName() { return exerciseName; }
        public int getSets() { return sets; }
        public int getReps() { return reps; }
    }
}