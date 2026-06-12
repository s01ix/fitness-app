package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.dto.GroupClassDTO;
import com.example.fitnessapp.dto.GymUserDTO;
import com.example.fitnessapp.dto.ReservationDTO;
import com.example.fitnessapp.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

public class ClientView extends VBox {

    private final PassTypeDaoJdbc passTypeDaoJdbc = new PassTypeDaoJdbc();
    private final GymPassDaoJdbc gymPassDaoJdbc = new GymPassDaoJdbc();
    private final TrainingPlanDaoJdbc trainingPlanDaoJdbc = new TrainingPlanDaoJdbc();
    private final PlanItemDaoJdbc planItemDaoJdbc = new PlanItemDaoJdbc();
    private final ExerciseDictDaoJdbc exerciseDictDaoJdbc = new ExerciseDictDaoJdbc();
    private final GroupClassDaoJdbc groupClassDaoJdbc = new GroupClassDaoJdbc();
    private final ReservationDAOJdbc reservationDAOJdbc = new ReservationDAOJdbc();
    private final GymUserDaoJdbc gymUserDaoJdbc = new GymUserDaoJdbc();
    private final MessageDaoJdbc messageDaoJdbc = new MessageDaoJdbc();
    private final int currentUserId;

    private final VBox activePassesContainer = new VBox(12);
    private final VBox trainingPlanContainer = new VBox(10);
    private final VBox attendanceHistoryContainer = new VBox(10);
    private final VBox inboxContainer = new VBox(10);

    public ClientView(NetworkClient networkClient, int currentUserId) {
        this.currentUserId = currentUserId;
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(25);

        this.setStyle("-fx-background-color: #f4f6f9;");

        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: #f4f6f9;");

        Label titleLabel = new Label("PANEL KLIENTA");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-family: 'Segoe UI', Arial;");

        //ZAKUP KARNETU PRZEZ KLIENTA
        VBox purchaseCard = new VBox(15);
        purchaseCard.setAlignment(Pos.CENTER);
        purchaseCard.setMaxWidth(450);

        purchaseCard.setStyle("-fx-background-color: #ffffff; -fx-padding: 25px; -fx-background-radius: 10px; " +
                "-fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 10px;");

        Label formTitle = new Label("Wykup nowy karnet");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a5568;");

        ComboBox<PassType> passTypeComboBox = new ComboBox<>();
        passTypeComboBox.setPromptText("Wybierz rodzaj karnetu...");
        passTypeComboBox.setPrefWidth(380);
        passTypeComboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 5px; -fx-padding: 3px;");

        List<PassType> availablePasses = passTypeDaoJdbc.findAll();
        passTypeComboBox.getItems().addAll(availablePasses);

        passTypeComboBox.setConverter(new StringConverter<PassType>() {
            @Override
            public String toString(PassType passType) {
                if (passType == null) return null;
                return passType.getName() + " — " + passType.getBasePrice() + " PLN";
            }
            @Override
            public PassType fromString(String string) { return null; }
        });

        ComboBox<String> paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.setPromptText("Wybierz metodę płatności...");
        paymentMethodComboBox.setPrefWidth(380);
        paymentMethodComboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 5px; -fx-padding: 3px;");
        paymentMethodComboBox.getItems().addAll("BLIK", "CARD", "TRANSFER", "CASH");

        Button buyButton = new Button("Sfinalizuj zakup");
        buyButton.setPrefWidth(380);

        buyButton.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 5px; -fx-padding: 10px; -fx-cursor: hand;");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        buyButton.setOnAction(e -> {
            PassType selectedPass = passTypeComboBox.getValue();
            String paymentMethod = paymentMethodComboBox.getValue();

            if (selectedPass == null || paymentMethod == null) {
                messageLabel.setText("Błąd: Wybierz karnet oraz metodę płatności!");
                messageLabel.setStyle("-fx-text-fill: #e53e3e;");
                return;
            }

            boolean success = gymPassDaoJdbc.purchasePass(
                    currentUserId,
                    selectedPass.getId(),
                    selectedPass.getBasePrice(),
                    paymentMethod
            );

            if (success) {
                messageLabel.setText("Sukces! Karnet został pomyślnie zakupiony.");
                messageLabel.setStyle("-fx-text-fill: #38a169;");
                passTypeComboBox.getSelectionModel().clearSelection();
                paymentMethodComboBox.getSelectionModel().clearSelection();
                refreshActivePasses();
            } else {
                messageLabel.setText("Wystąpił błąd podczas transakcji. Spróbuj ponownie.");
                messageLabel.setStyle("-fx-text-fill: #e53e3e;");
            }
        });

        purchaseCard.getChildren().addAll(formTitle, passTypeComboBox, paymentMethodComboBox, buyButton, messageLabel);

        Label activePassesTitle = new Label("Twoje aktywne karnety");
        activePassesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 15 0 0 0;");

        activePassesContainer.setAlignment(Pos.TOP_CENTER);
        activePassesContainer.setMaxWidth(450);

        refreshActivePasses();
        Label trainingPlanTitle = new Label("Twój aktualny plan treningowy");
        trainingPlanTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 15 0 0 0;");

        trainingPlanContainer.setAlignment(Pos.TOP_CENTER);
        trainingPlanContainer.setMaxWidth(450);

        refreshTrainingPlan();

        //ZAPISY NA ZAJĘCIA GRUPOWE
        VBox classEnrollCard = new VBox(15);
        classEnrollCard.setAlignment(Pos.CENTER);
        classEnrollCard.setMaxWidth(450);
        classEnrollCard.setStyle("-fx-background-color: #ffffff; -fx-padding: 25px; -fx-background-radius: 10px; " +
                "-fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 10px;");

        Label classEnrollTitle = new Label("Zapisz się na zajęcia grupowe");
        classEnrollTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a5568;");

        ComboBox<GroupClassDTO> groupClassComboBox = new ComboBox<>();
        groupClassComboBox.setPromptText("Wybierz zajęcia...");
        groupClassComboBox.setPrefWidth(380);
        groupClassComboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 5px; -fx-padding: 3px;");

        List<GroupClassDTO> availableClasses = groupClassDaoJdbc.getAvailableClassesWithDetails();
        groupClassComboBox.getItems().addAll(availableClasses);

        groupClassComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(GroupClassDTO gc) {
                if (gc == null) return null;
                int booked = reservationDAOJdbc.countConfirmedReservations(gc.getId());
                return String.format("%s (%s) - Trener: %s [%d/%d miejsc]",
                        gc.getName(),
                        gc.getScheduleTime().toLocalDate().toString(),
                        gc.getTrainerName(),
                        booked,
                        gc.getCapacity());
            }
            @Override
            public GroupClassDTO fromString(String string) { return null; }
        });

        Button enrollButton = new Button("Zapisz się");
        enrollButton.setPrefWidth(380);
        enrollButton.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 5px; -fx-padding: 10px; -fx-cursor: hand;");

        Label enrollMessage = new Label();
        enrollMessage.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        enrollButton.setOnAction(e -> {
            GroupClassDTO selectedClass = groupClassComboBox.getValue();

            if (selectedClass == null) {
                enrollMessage.setText("Błąd: Wybierz zajęcia z listy!");
                enrollMessage.setStyle("-fx-text-fill: #e53e3e;");
                return;
            }

            if (!reservationDAOJdbc.hasAvailableSpots(selectedClass.getId())) {
                enrollMessage.setText("Przepraszamy, brak wolnych miejsc na te zajęcia.");
                enrollMessage.setStyle("-fx-text-fill: #e53e3e;");
                return;
            }

            com.example.fitnessapp.model.Reservation reservation = new com.example.fitnessapp.model.Reservation(
                    0, currentUserId, selectedClass.getId(), 0, java.time.LocalDate.now(), "CONFIRMED"
            );

            try {
                reservationDAOJdbc.save(reservation);
                enrollMessage.setText("Sukces! Zostałeś zapisany na zajęcia.");
                enrollMessage.setStyle("-fx-text-fill: #38a169;");
                groupClassComboBox.getSelectionModel().clearSelection();

                groupClassComboBox.getItems().clear();
                groupClassComboBox.getItems().addAll(groupClassDaoJdbc.getAvailableClassesWithDetails());
            } catch (Exception ex) {
                enrollMessage.setText("Wystąpił błąd podczas zapisu.");
                enrollMessage.setStyle("-fx-text-fill: #e53e3e;");
                ex.printStackTrace();
            }
        });

        classEnrollCard.getChildren().addAll(classEnrollTitle, groupClassComboBox, enrollButton, enrollMessage);

        //REZERWACJA TRENINGU PERSONALNEGO
        VBox personalTrainingCard = new VBox(15);
        personalTrainingCard.setAlignment(Pos.CENTER);
        personalTrainingCard.setMaxWidth(450);
        personalTrainingCard.setStyle("-fx-background-color: #ffffff; -fx-padding: 25px; -fx-background-radius: 10px; " +
                "-fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 10px;");

        Label ptTitle = new Label("Umów trening personalny");
        ptTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a5568;");

        ComboBox<com.example.fitnessapp.dto.GymUserDTO> trainerComboBox = new ComboBox<>();
        trainerComboBox.setPromptText("Wybierz trenera...");
        trainerComboBox.setPrefWidth(380);
        trainerComboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 5px; -fx-padding: 3px;");

        trainerComboBox.getItems().addAll(gymUserDaoJdbc.findAllTrainers());

        trainerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(com.example.fitnessapp.dto.GymUserDTO trainer) {
                return trainer == null ? null : trainer.getFullName();
            }
            @Override
            public com.example.fitnessapp.dto.GymUserDTO fromString(String string) { return null; }
        });

        javafx.scene.control.DatePicker datePicker = new javafx.scene.control.DatePicker();
        datePicker.setPromptText("Wybierz datę treningu...");
        datePicker.setPrefWidth(380);
        datePicker.setStyle("-fx-font-size: 13px;");

        Button bookPtButton = new Button("Rezerwuj termin");
        bookPtButton.setPrefWidth(380);
        bookPtButton.setStyle("-fx-background-color: #d69e2e; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 5px; -fx-padding: 10px; -fx-cursor: hand;");

        Label ptMessage = new Label();
        ptMessage.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        bookPtButton.setOnAction(e -> {
            GymUserDTO selectedTrainer = trainerComboBox.getValue();
            java.time.LocalDate selectedDate = datePicker.getValue();

            if (selectedTrainer == null || selectedDate == null) {
                ptMessage.setText("Błąd: Wybierz trenera oraz datę!");
                ptMessage.setStyle("-fx-text-fill: #e53e3e;");
                return;
            }

            if (selectedDate.isBefore(java.time.LocalDate.now())) {
                ptMessage.setText("Błąd: Nie możesz wybrać daty z przeszłości!");
                ptMessage.setStyle("-fx-text-fill: #e53e3e;");
                return;
            }

            Reservation reservation = new Reservation(
                    0, currentUserId, 0, selectedTrainer.getId(), selectedDate, "CONFIRMED"
            );

            try {
                reservationDAOJdbc.save(reservation);
                ptMessage.setText("Sukces! Trening został pomyślnie zarezerwowany.");
                ptMessage.setStyle("-fx-text-fill: #38a169;");
                trainerComboBox.getSelectionModel().clearSelection();
                datePicker.setValue(null);

                refreshAttendanceHistory();
            } catch (Exception ex) {
                ptMessage.setText("Wystąpił błąd podczas rezerwacji.");
                ptMessage.setStyle("-fx-text-fill: #e53e3e;");
                ex.printStackTrace();
            }
        });

        personalTrainingCard.getChildren().addAll(ptTitle, trainerComboBox, datePicker, bookPtButton, ptMessage);

        //HISTORIA WIZYT I FREKWENCJA
        Label historyTitle = new Label("Twoja frekwencja i historia wizyt");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 15 0 0 0;");

        attendanceHistoryContainer.setAlignment(Pos.TOP_CENTER);
        attendanceHistoryContainer.setMaxWidth(450);

        refreshAttendanceHistory();

        //SKRZYNKA ODBIORCZA OD TRENERA
        Label inboxTitle = new Label("Skrzynka odbiorcza - Wiadomości od trenera");
        inboxTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 15 0 0 0;");

        inboxContainer.setAlignment(Pos.TOP_CENTER);
        inboxContainer.setMaxWidth(450);

        refreshInbox();

        contentBox.getChildren().addAll(
                titleLabel,
                purchaseCard,
                activePassesTitle,
                activePassesContainer,
                classEnrollCard,
                personalTrainingCard,
                historyTitle,
                attendanceHistoryContainer,
                trainingPlanTitle,
                trainingPlanContainer,
                inboxTitle,
                inboxContainer
        );

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        this.getChildren().add(scrollPane);
    }

    private void showPassDetails(GymPass pass, String passName) {

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły Twojego Karnetu");
        alert.setHeaderText(passName.toUpperCase());

        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), pass.getExpirationDate());
        String remainingDaysText = daysLeft > 0 ? daysLeft + " dni" : "Karnet wygasł";

        String details = String.format(
                " Identyfikator karnetu (ID):  %d\n\n" +
                        " Status:  %s\n\n" +
                        " Cena zakupu:  %s PLN\n\n" +
                        " Data aktywacji:  %s\n\n" +
                        " Data wygaśnięcia:  %s\n\n" +
                        " Pozostało ważności:  %s",
                pass.getId(),
                pass.getStatus(),
                pass.getPrice().toString(),
                pass.getPurchaseDate().toString(),
                pass.getExpirationDate().toString(),
                remainingDaysText
        );

        alert.setContentText(details);

        alert.showAndWait();
    }

    private void refreshActivePasses() {
        activePassesContainer.getChildren().clear();

        List<GymPass> activePasses = gymPassDaoJdbc.findActiveByUserId(currentUserId);

        if (activePasses.isEmpty()) {
            Label noPassesLabel = new Label("Nie posiadasz obecnie żadnych aktywnych karnetów.");
            noPassesLabel.setStyle("-fx-text-fill: #718096; -fx-font-style: italic; -fx-font-size: 13px;");
            activePassesContainer.getChildren().add(noPassesLabel);
            return;
        }

        for (GymPass pass : activePasses) {
            String passName = passTypeDaoJdbc.findById(pass.getPassTypeId())
                    .map(PassType::getName)
                    .orElse("Karnet");

            long daysLeft = pass.getDaysRemaining(java.time.LocalDate.now());

            String passInfo;
            String style;

            if (daysLeft > 7) {
                passInfo = String.format("%s\nPozostało: %d dni (ważny do: %s)\nCena: %s PLN",
                        passName, daysLeft, pass.getExpirationDate(), pass.getPrice());
                style = "-fx-background-color: #f0fff4; -fx-text-fill: #276749; -fx-border-color: #c6f6d5;";
            } else if (daysLeft > 0) {
                passInfo = String.format("⚠️ UWAGA: Karnet wygasa za %d dni!\n%s\n(ważny do: %s)",
                        daysLeft, passName, pass.getExpirationDate());
                style = "-fx-background-color: #fffff0; -fx-text-fill: #b7791f; -fx-border-color: #f6e05e;";
            } else {
                passInfo = String.format("❌ Karnet wygasł!\n%s", passName);
                style = "-fx-background-color: #fff5f5; -fx-text-fill: #c53030; -fx-border-color: #feb2b2;";
            }

            Label passLabel = new Label(passInfo);

            passLabel.setPrefWidth(450);
            passLabel.setWrapText(true);
            passLabel.setAlignment(Pos.CENTER);

            passLabel.setStyle(style + " -fx-padding: 12px; -fx-background-radius: 8px; " +
                    "-fx-border-width: 1px; -fx-border-radius: 8px; -fx-font-size: 13px; " +
                    "-fx-font-weight: bold; -fx-text-alignment: center; -fx-cursor: hand;");

            passLabel.setOnMouseClicked(e -> showPassDetails(pass, passName));

            activePassesContainer.getChildren().add(passLabel);
        }
    }
    private void refreshTrainingPlan() {
        trainingPlanContainer.getChildren().clear();

        List<TrainingPlan> plans = trainingPlanDaoJdbc.findByUserId(currentUserId);

        if (plans.isEmpty()) {
            Label noPlanLabel = new Label("Trener nie udostępnił Ci jeszcze żadnego planu.");
            noPlanLabel.setStyle("-fx-text-fill: #718096; -fx-font-style: italic; -fx-font-size: 13px;");
            trainingPlanContainer.getChildren().add(noPlanLabel);
            return;
        }

        for (TrainingPlan plan : plans) {
            Label planNameLabel = new Label("Plan: " + plan.getGoal());
            planNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3182ce; -fx-font-size: 14px; -fx-padding: 0 0 5 0;");
            trainingPlanContainer.getChildren().add(planNameLabel);

            VBox exercisesBox = new VBox(10);
            exercisesBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 18px; -fx-background-radius: 10px; " +
                    "-fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 10px;");
            exercisesBox.setPrefWidth(450);

            List<PlanItem> items = planItemDaoJdbc.findByPlanId(plan.getId());

            if (items.isEmpty()) {
                Label emptyPlanLabel = new Label("Plan jest pusty (brak przypisanych ćwiczeń).");
                emptyPlanLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-style: italic; -fx-font-size: 12px;");
                exercisesBox.getChildren().add(emptyPlanLabel);
            } else {
                for (PlanItem item : items) {
                    String exerciseName = exerciseDictDaoJdbc.findById(item.getExerciseId())
                            .map(ExerciseDict::getName)
                            .orElse("Nieznane ćwiczenie");

                    String exerciseText = String.format("• %s  —  %dx%d",
                            exerciseName,
                            item.getSets(),
                            item.getReps()
                    );
                    Label exLabel = new Label(exerciseText);
                    exLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d3748;");
                    exercisesBox.getChildren().add(exLabel);
                }
            }

            trainingPlanContainer.getChildren().add(exercisesBox);

            Region spacer = new Region();
            spacer.setPrefHeight(10);
            trainingPlanContainer.getChildren().add(spacer);
        }
    }

    private void refreshAttendanceHistory() {
        attendanceHistoryContainer.getChildren().clear();

        List<ReservationDTO> history = reservationDAOJdbc.getClientReservationHistory(currentUserId);

        if (history.isEmpty()) {
            Label noHistoryLabel = new Label("Brak odnotowanych wizyt oraz rezerwacji.");
            noHistoryLabel.setStyle("-fx-text-fill: #718096; -fx-font-style: italic; -fx-font-size: 13px;");
            attendanceHistoryContainer.getChildren().add(noHistoryLabel);
            return;
        }

        for (ReservationDTO res : history) {
            String displayText = String.format("[%s] %s\nData: %s  |  Status: %s",
                    res.getType(),
                    res.getEventName(),
                    res.getDate().toString(),
                    res.getStatus()
            );

            Label rowLabel = new Label(displayText);
            rowLabel.setPrefWidth(450);
            rowLabel.setWrapText(true);
            rowLabel.setAlignment(Pos.CENTER_LEFT);

            String bgStyle = "#f7fafc";
            String textStyle = "#4a5568";
            String borderStyle = "#e2e8f0";

            if ("CONFIRMED".equals(res.getStatus()) || "COMPLETED".equals(res.getStatus())) {
                bgStyle = "#ebf8ff";
                textStyle = "#2b6cb0";
                borderStyle = "#bee3f8";
            } else if ("CANCELED".equals(res.getStatus())) {
                bgStyle = "#fff5f5";
                textStyle = "#c53030";
                borderStyle = "#fed7d7";
            }

            rowLabel.setStyle(String.format(
                    "-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 10px 15px; " +
                            "-fx-background-radius: 8px; -fx-border-color: %s; -fx-border-width: 1px; " +
                            "-fx-border-radius: 8px; -fx-font-size: 13px; -fx-font-weight: bold;",
                    bgStyle, textStyle, borderStyle));

            attendanceHistoryContainer.getChildren().add(rowLabel);
        }
    }

    private void refreshInbox() {
        inboxContainer.getChildren().clear();

        List<Message> messages = messageDaoJdbc.findReceivedMessages(currentUserId);

        if (messages.isEmpty()) {
            Label noMessagesLabel = new Label("Skrzynka jest pusta. Nie masz nowych wiadomości.");
            noMessagesLabel.setStyle("-fx-text-fill: #718096; -fx-font-style: italic; -fx-font-size: 13px;");
            inboxContainer.getChildren().add(noMessagesLabel);
            return;
        }

        for (Message msg : messages) {
            String senderName = gymUserDaoJdbc.findById(msg.getSenderId())
                    .map(u -> "Trener " + u.getFirstName() + " " + u.getLastName())
                    .orElse("Nieznany Nadawca");

            VBox messageCard = new VBox(8);
            messageCard.setPadding(new Insets(15));
            messageCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; " +
                    "-fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            messageCard.setPrefWidth(450);

            String dateStr = msg.getSentAt() != null ? msg.getSentAt().toLocalDate().toString() : "Brak daty";
            Label headerLabel = new Label("Od: " + senderName + "   |   " + dateStr);
            headerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096; -fx-font-weight: bold;");

            Label contentLabel = new Label(msg.getContent());
            contentLabel.setWrapText(true);
            contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");

            messageCard.getChildren().addAll(headerLabel, contentLabel);
            inboxContainer.getChildren().add(messageCard);
        }
    }

}