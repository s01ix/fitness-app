package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import com.example.fitnessapp.dao.*;
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
    private final int currentUserId;

    private final VBox activePassesContainer = new VBox(12);
    private final VBox trainingPlanContainer = new VBox(10);

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

        contentBox.getChildren().addAll(
                titleLabel,
                purchaseCard,
                activePassesTitle,
                activePassesContainer,
                trainingPlanTitle,
                trainingPlanContainer
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

            String passInfo = String.format("%s\nWażny do: %s   |   Cena: %s PLN",
                    passName,
                    pass.getExpirationDate().toString(),
                    pass.getPrice().toString()
            );

            Label passLabel = new Label(passInfo);

            passLabel.setPrefWidth(450);
            passLabel.setWrapText(true);
            passLabel.setAlignment(Pos.CENTER);

            passLabel.setStyle("-fx-background-color: #f0fff4; -fx-text-fill: #276749; -fx-padding: 12px; " +
                    "-fx-background-radius: 8px; -fx-border-color: #c6f6d5; -fx-border-width: 1px; " +
                    "-fx-border-radius: 8px; -fx-font-size: 13px; -fx-font-weight: bold; " +
                    "-fx-text-alignment: center; -fx-cursor: hand;");

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
}