package com.example.fitnessapp.view;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.example.fitnessapp.NetworkClient;

public class AdminView extends VBox {
    private NetworkClient networkClient;
    private int currentUserId;

    private Button getClubsBtn, addClubBtn;
    private TextArea clubDisplay;
    private TextField clubName, clubAddress, clubHours;
    private Button getEqBtn, addEqBtn;
    private TextArea eqDisplay;
    private TextField eqClubId, eqName;
    private ComboBox<String> eqStatus;
    private DatePicker eqDate;

    public AdminView(NetworkClient networkClient, int currentUserId) {
        this.networkClient = networkClient;
        this.currentUserId = currentUserId;

        VBox clubBox = buildClubModule();
        VBox dataBox = buildEquipmentModule();

        setupClubActions();
        setupEquipmentActions();

        this.getChildren().addAll(clubBox, dataBox);
    }

    private VBox buildClubModule() {
        VBox clubBox = new VBox(5);
        clubBox.setStyle("-fx-border-color: orange; -fx-padding: 10;");

        getClubsBtn = new Button("Pokaż kluby");
        clubDisplay = new TextArea();
        clubDisplay.setPrefHeight(100);
        clubName = new TextField(); clubName.setPromptText("Nazwa klubu");
        clubAddress = new TextField(); clubAddress.setPromptText("Adres");
        clubHours = new TextField(); clubHours.setPromptText("Godziny otwarcia");
        addClubBtn = new Button("Dodaj klub");

        clubBox.getChildren().addAll(
                new Label("Moduł Klubów:"), getClubsBtn, clubDisplay,
                new Label("Dodaj nowy klub:"), clubName, clubAddress, clubHours, addClubBtn
        );
        return clubBox;
    }

    private VBox buildEquipmentModule() {
        VBox dataBox = new VBox(5);
        dataBox.setStyle("-fx-border-color: blue; -fx-padding: 10;");
        getEqBtn = new Button("Pokaż sprzęt");
        eqDisplay = new TextArea();
        eqDisplay.setPrefHeight(100);
        eqClubId = new TextField(); eqClubId.setPromptText("ID Klubu");
        eqName = new TextField(); eqName.setPromptText("Nazwa sprzętu");
        eqStatus = new ComboBox<>();
        eqStatus.getItems().addAll("OPERATIONAL", "MAINTENANCE", "BROKEN");
        eqStatus.setPromptText("Status");
        eqStatus.setValue("OPERATIONAL");
        eqDate = new DatePicker();
        eqDate.setPromptText("Data inspekcji");
        eqDate.setValue(java.time.LocalDate.now());
        addEqBtn = new Button("Dodaj sprzęt");
        dataBox.getChildren().addAll(
                new Label("Moduł Sprzętu:"), getEqBtn, eqDisplay,
                new Label("Dodaj nowy sprzęt:"), eqClubId, eqName, eqStatus, eqDate, addEqBtn
        );
        return dataBox;
    }

    private void setupClubActions() {
        getClubsBtn.setOnAction(e -> {
            clubDisplay.setText("Pobieranie danych...");
            new Thread(() -> {
                String resp = networkClient.sendRequest("GET_CLUBS");
                System.out.println("Serwer zwrócił (Kluby): " + resp);
                Platform.runLater(() -> {
                    clubDisplay.clear();
                    if (resp != null && resp.startsWith("CLUBS_OK")) {
                        String[] tokens = resp.split(";");
                        if (tokens.length <= 1) {
                            clubDisplay.setText("Baza klubów jest pusta.");
                        } else {
                            for (int i = 1; i < tokens.length; i++) {
                                String[] data = tokens[i].split(",");
                                if (data.length >= 3) {
                                    clubDisplay.appendText("ID: " + data[0] + " | Nazwa: " + data[1] + " | Adres: " + data[2] + (data.length > 3 ? " | Godziny: " + data[3] : "") + "\n");
                                } else {
                                    clubDisplay.appendText("Dane: " + tokens[i] + "\n");
                                }
                            }
                        }
                    } else {
                        clubDisplay.setText("Błąd serwera: " + resp);
                    }
                });
            }).start();
        });

        addClubBtn.setOnAction(e -> {
            String name = clubName.getText();
            String address = clubAddress.getText();
            String hours = clubHours.getText();

            new Thread(() -> {
                String req = String.format("ADD_CLUB;%s;%s;%s", name, address, hours);
                String resp = networkClient.sendRequest(req);
                System.out.println("Odpowiedź dodawania klubu: " + (resp == null ? "poprawnie dodano" : resp));
                Platform.runLater(() -> {
                    Alert alert = new Alert(resp != null && resp.startsWith("ADD_CLUB_OK") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                    alert.setContentText(resp != null && resp.contains(";") ? resp.split(";")[1] : (resp == null ? "Błąd" : resp));
                    alert.showAndWait();
                    if (resp != null && resp.startsWith("ADD_CLUB_OK")) {
                        getClubsBtn.fire();
                    }
                });
            }).start();
        });
    }

    private void setupEquipmentActions() {
        getEqBtn.setOnAction(e -> {
            eqDisplay.setText("Pobieranie danych...");
            new Thread(() -> {
                String resp = networkClient.sendRequest("GET_EQUIPMENT");
                System.out.println("Serwer zwrócił (Sprzęt): " + resp);
                Platform.runLater(() -> {
                    eqDisplay.clear();
                    if (resp != null && resp.startsWith("EQUIPMENT_OK")) {
                        String[] tokens = resp.split(";");
                        if (tokens.length <= 1) {
                            eqDisplay.setText("Brak sprzętu w bazie.");
                        } else {
                            for (int i = 1; i < tokens.length; i++) {
                                eqDisplay.appendText(tokens[i] + "\n");
                            }
                        }
                    } else {
                        eqDisplay.setText("Błąd serwera: " + resp);
                    }
                });
            }).start();
        });

        addEqBtn.setOnAction(e -> {
            String clubId = eqClubId.getText();
            String name = eqName.getText();
            String status = eqStatus.getValue();
            String dateStr = eqDate.getValue() != null ? eqDate.getValue().toString() : "";
            new Thread(() -> {
                String req = String.format("ADD_EQUIPMENT;%s;%s;%s;%s", clubId, name, status, dateStr);
                String resp = networkClient.sendRequest(req);
                System.out.println("Odpowiedź dodawania sprzętu: " + (resp == null ? "poprawnie dodano" : resp));
                Platform.runLater(() -> { getEqBtn.fire();});
            }).start();
        });
    }
}