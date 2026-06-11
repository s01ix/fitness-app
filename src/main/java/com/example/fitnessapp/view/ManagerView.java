package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ManagerView extends VBox {

    private final NetworkClient networkClient;
    private final int currentUserId;

    private TextArea campaignsDisplay, discountDisplay, cPassDisplay;
    private Label totalRevenueLabel, totalUsersLabel, totalTransactionsLabel;

    public ManagerView(NetworkClient networkClient, int currentUserId) {
        this.networkClient = networkClient;
        this.currentUserId = currentUserId;

        this.setPadding(new Insets(15));
        this.setSpacing(15);

        Label titleLabel = new Label("PANEL MANAGERA KLUBU");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        this.getChildren().add(titleLabel);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab statsTab = new Tab("Raporty i Finanse", buildStatsModule());
        Tab campaignsTab = new Tab("Kampanie Promocyjne", buildCampaignsModule());
        Tab discountsTab = new Tab("Zniżki i Promowane Karnety", buildDiscountsModule());

        tabPane.getTabs().addAll(statsTab, campaignsTab, discountsTab);
        this.getChildren().add(tabPane);

        refreshStats(); refreshCampaigns(); refreshDiscounts(); refreshCPasses();
    }

    private VBox buildStatsModule() {
        VBox box = new VBox(20); box.setPadding(new Insets(20));
        GridPane grid = new GridPane(); grid.setHgap(30); grid.setVgap(15);
        
        totalRevenueLabel = new Label("0.00 PLN"); totalRevenueLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2f855a;");
        totalUsersLabel = new Label("0"); totalUsersLabel.setStyle("-fx-font-weight: bold;");
        totalTransactionsLabel = new Label("0"); totalTransactionsLabel.setStyle("-fx-font-weight: bold;");

        grid.add(new Label("Całkowity przychód klubu (z płatności):"), 0, 0); grid.add(totalRevenueLabel, 1, 0);
        grid.add(new Label("Zarejestrowani członkowie:"), 0, 1); grid.add(totalUsersLabel, 1, 1);
        grid.add(new Label("Liczba płatności:"), 0, 2); grid.add(totalTransactionsLabel, 1, 2);

        Button refreshBtn = new Button("Odśwież raporty finansowe");
        refreshBtn.setOnAction(e -> refreshStats());
        box.getChildren().addAll(new Label("Kluczowe wskaźniki (KPI)"), grid, refreshBtn);
        return box;
    }

    private VBox buildCampaignsModule() {
        VBox box = new VBox(15); box.setPadding(new Insets(20));
        HBox layout = new HBox(20);

        VBox form = new VBox(10); form.setPrefWidth(300);
        TextField nameField = new TextField(); nameField.setPromptText("Nazwa kampanii");
        TextField targetField = new TextField(); targetField.setPromptText("Grupa docelowa");
        TextField budgetField = new TextField(); budgetField.setPromptText("Budżet");
        DatePicker startDatePicker = new DatePicker(); startDatePicker.setPromptText("Data rozpoczęcia");
        DatePicker endDatePicker = new DatePicker(); endDatePicker.setPromptText("Data zakończenia");
        
        Button saveBtn = new Button("Uruchom kampanię");
        saveBtn.setOnAction(e -> {
            new Thread(() -> {
                String req = String.format("ADD_CAMPAIGN;%s;%s;%s;%s;%s", nameField.getText(), targetField.getText(), budgetField.getText(), startDatePicker.getValue(), endDatePicker.getValue());
                String resp = networkClient.sendRequest(req);
                Platform.runLater(() -> { showAlert("Status operacji", resp); refreshCampaigns(); });
            }).start();
        });
        form.getChildren().addAll(new Label("Nowa kampania"), nameField, targetField, budgetField, startDatePicker, endDatePicker, saveBtn);

        VBox preview = new VBox(10);
        campaignsDisplay = new TextArea(); campaignsDisplay.setEditable(false); campaignsDisplay.setPrefSize(400, 200);
        
        HBox deleteBar = new HBox(10);
        TextField deleteCampaignIdField = new TextField(); deleteCampaignIdField.setPromptText("ID do usunięcia");
        deleteCampaignIdField.setPrefWidth(120);
        Button deleteCampBtn = new Button("Usuń kampanię");
        deleteCampBtn.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteCampBtn.setOnAction(e -> {
            String id = deleteCampaignIdField.getText().trim();
            if(!id.isEmpty()) {
                new Thread(() -> {
                    String resp = networkClient.sendRequest("DELETE_CAMPAIGN;" + id);
                    Platform.runLater(() -> { showAlert("Status usuwania", resp); deleteCampaignIdField.clear(); refreshCampaigns(); });
                }).start();
            }
        });
        deleteBar.getChildren().addAll(deleteCampaignIdField, deleteCampBtn);
        
        Button refreshCampBtn = new Button("Odśwież listę"); refreshCampBtn.setOnAction(e -> refreshCampaigns());
        preview.getChildren().addAll(new Label("Aktywne kampanie"), campaignsDisplay, deleteBar, refreshCampBtn);

        layout.getChildren().addAll(form, preview); box.getChildren().add(layout);
        return box;
    }

    private VBox buildDiscountsModule() {
        VBox box = new VBox(15); box.setPadding(new Insets(20));
        HBox layout = new HBox(20);

        VBox discountForm = new VBox(10); discountForm.setPrefWidth(320);
        TextField dCampId = new TextField(); dCampId.setPromptText("ID Kampanii");
        TextField dType = new TextField(); dType.setPromptText("Typ (PERCENTAGE / AMOUNT)");
        TextField dValue = new TextField(); dValue.setPromptText("Wartość zniżki");
        Button addDiscountBtn = new Button("Dodaj zniżkę");
        discountDisplay = new TextArea(); discountDisplay.setPrefHeight(150); discountDisplay.setEditable(false);
        
        HBox deleteDBar = new HBox(10);
        TextField deleteDiscountId = new TextField(); deleteDiscountId.setPromptText("ID zniżki"); deleteDiscountId.setPrefWidth(100);
        Button deleteDBtn = new Button("Usuń zniżkę"); deleteDBtn.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteDBtn.setOnAction(e -> {
            if(!deleteDiscountId.getText().isEmpty()) {
                new Thread(() -> {
                    String resp = networkClient.sendRequest("DELETE_DISCOUNT;" + deleteDiscountId.getText().trim());
                    Platform.runLater(() -> { showAlert("Status usuwania", resp); deleteDiscountId.clear(); refreshDiscounts(); });
                }).start();
            }
        });
        deleteDBar.getChildren().addAll(deleteDiscountId, deleteDBtn);

        addDiscountBtn.setOnAction(e -> {
            new Thread(() -> {
                String req = String.format("ADD_DISCOUNT;%s;%s;%s", dCampId.getText(), dType.getText(), dValue.getText());
                String resp = networkClient.sendRequest(req);
                Platform.runLater(() -> { showAlert("Status dodawania", resp); refreshDiscounts(); });
            }).start();
        });
        discountForm.getChildren().addAll(new Label("Zniżki w kampaniach"), dCampId, dType, dValue, addDiscountBtn, discountDisplay, deleteDBar);

        VBox cPassForm = new VBox(10); cPassForm.setPrefWidth(320);
        TextField cpCampId = new TextField(); cpCampId.setPromptText("ID Kampanii");
        TextField cpPassId = new TextField(); cpPassId.setPromptText("ID Karnetu (PassType)");
        Button addCPassBtn = new Button("Przypisz karnet");
        cPassDisplay = new TextArea(); cPassDisplay.setPrefHeight(150); cPassDisplay.setEditable(false);

        HBox deleteCPBar = new HBox(10);
        TextField deleteCPassId = new TextField(); deleteCPassId.setPromptText("ID powiązania"); deleteCPassId.setPrefWidth(100);
        Button deleteCPBtn = new Button("Usuń powiązanie"); deleteCPBtn.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteCPBtn.setOnAction(e -> {
            if(!deleteCPassId.getText().isEmpty()) {
                new Thread(() -> {
                    String resp = networkClient.sendRequest("DELETE_CAMPAIGN_PASS;" + deleteCPassId.getText().trim());
                    Platform.runLater(() -> { showAlert("Status usuwania", resp); deleteCPassId.clear(); refreshCPasses(); });
                }).start();
            }
        });
        deleteCPBar.getChildren().addAll(deleteCPassId, deleteCPBtn);

        addCPassBtn.setOnAction(e -> {
            new Thread(() -> {
                String req = String.format("ADD_CAMPAIGN_PASS;%s;%s", cpCampId.getText(), cpPassId.getText());
                String resp = networkClient.sendRequest(req);
                Platform.runLater(() -> { showAlert("Status operacji", resp); refreshCPasses(); });
            }).start();
        });
        cPassForm.getChildren().addAll(new Label("Promowane karnety"), cpCampId, cpPassId, addCPassBtn, cPassDisplay, deleteCPBar);

        layout.getChildren().addAll(discountForm, cPassForm); box.getChildren().add(layout);
        return box;
    }

    private void refreshStats() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_MGR_STATS");
            Platform.runLater(() -> {
                if (resp != null && resp.startsWith("MGR_STATS_OK")) {
                    String[] t = resp.split(";");
                    if(t.length >= 4) {
                        totalRevenueLabel.setText(t[1] + " PLN"); totalUsersLabel.setText(t[2]); totalTransactionsLabel.setText(t[3]);
                    }
                }
            });
        }).start();
    }

    private void refreshCampaigns() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_CAMPAIGNS");
            Platform.runLater(() -> {
                campaignsDisplay.clear();
                if (resp != null && resp.startsWith("CAMPAIGNS_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] p = tokens[i].split("\\|");
                        if(p.length >= 4) {
                            campaignsDisplay.appendText("ID: " + p[0] + " | " + p[1] + " | Budzet: " + p[3] + " PLN\n");
                        }
                    }
                }
            });
        }).start();
    }

    private void refreshDiscounts() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_DISCOUNTS");
            Platform.runLater(() -> {
                discountDisplay.clear();
                if (resp != null && resp.startsWith("DISCOUNTS_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        discountDisplay.appendText(tokens[i] + "\n");
                    }
                }
            });
        }).start();
    }

    private void refreshCPasses() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_CAMPAIGN_PASSES");
            Platform.runLater(() -> {
                cPassDisplay.clear();
                if (resp != null && resp.startsWith("CPASSES_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        cPassDisplay.appendText(tokens[i] + "\n");
                    }
                }
            });
        }).start();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); 
        if (msg != null && msg.contains(";")) {
            alert.setContentText(msg.split(";")[1]);
        } else {
            alert.setContentText(msg != null ? msg : "Brak odpowiedzi sieci.");
        }
        alert.showAndWait();
    }
}