package com.example.fitnessapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FitnessApp extends Application {
    private NetworkClient networkClient;
    private int currentUserId = -1;

    @Override
    public void start(Stage stage) {
        networkClient = new NetworkClient();
        stage.setTitle("System Fitness");

        // --- 1. Logowanie ---
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        
        TextField emailField = new TextField(); emailField.setPromptText("Email");
        PasswordField passField = new PasswordField(); passField.setPromptText("Hasło");
        Button loginBtn = new Button("Zaloguj");
        Label statusLabel = new Label();
        
        loginGrid.add(new Label("Email:"), 0, 0); loginGrid.add(emailField, 1, 0);
        loginGrid.add(new Label("Hasło:"), 0, 1); loginGrid.add(passField, 1, 1);
        loginGrid.add(loginBtn, 1, 2);
        loginGrid.add(statusLabel, 1, 3);

        // --- 2. Rejestracja ---
        VBox regBox = new VBox(5);
        regBox.setStyle("-fx-border-color: gray; -fx-padding: 10;");
        TextField regPesel = new TextField(); regPesel.setPromptText("PESEL");
        TextField regFn = new TextField(); regFn.setPromptText("Imię");
        TextField regLn = new TextField(); regLn.setPromptText("Nazwisko");
        TextField regEmail = new TextField(); regEmail.setPromptText("Email");
        PasswordField regPass = new PasswordField(); regPass.setPromptText("Hasło");
        ComboBox<String> regRole = new ComboBox<>();
        regRole.getItems().addAll("CLIENT", "TRAINER", "RECEPTIONIST");
        regRole.setValue("CLIENT");
        Button regBtn = new Button("Zarejestruj");
        regBox.getChildren().addAll(new Label("Rejestracja nowego konta:"), regPesel, regFn, regLn, regEmail, regPass, regRole, regBtn);

        // --- 2.5. Moduł Klubów ---
        VBox clubBox = new VBox(5);
        clubBox.setStyle("-fx-border-color: orange; -fx-padding: 10;");
        Button getClubsBtn = new Button("Pokaż kluby");
        TextArea clubDisplay = new TextArea();
        clubDisplay.setPrefHeight(100);

        TextField clubName = new TextField(); clubName.setPromptText("Nazwa klubu");
        TextField clubAddress = new TextField(); clubAddress.setPromptText("Adres");
        TextField clubHours = new TextField(); clubHours.setPromptText("Godziny otwarcia");
        Button addClubBtn = new Button("Dodaj klub");

        clubBox.getChildren().addAll(new Label("Moduł Klubów:"), getClubsBtn, clubDisplay, new Label("Dodaj nowy klub:"), clubName, clubAddress, clubHours, addClubBtn);

        // --- 3. Moduł Sprzętu ---
        VBox dataBox = new VBox(5);
        dataBox.setStyle("-fx-border-color: blue; -fx-padding: 10;");
        Button getEqBtn = new Button("Pokaż sprzęt");
        TextArea eqDisplay = new TextArea();
        eqDisplay.setPrefHeight(100);

        TextField eqClubId = new TextField(); eqClubId.setPromptText("ID Klubu");
        TextField eqName = new TextField(); eqName.setPromptText("Nazwa sprzętu");

        ComboBox<String> eqStatus = new ComboBox<>();
        eqStatus.getItems().addAll("OPERATIONAL", "MAINTENANCE", "BROKEN");
        eqStatus.setPromptText("Status");
        eqStatus.setValue("OPERATIONAL");
        
        DatePicker eqDate = new DatePicker();
        eqDate.setPromptText("Data inspekcji");
        eqDate.setValue(java.time.LocalDate.now());
        
        Button addEqBtn = new Button("Dodaj sprzęt");

        dataBox.getChildren().addAll(new Label("Moduł Sprzętu:"), getEqBtn, eqDisplay, new Label("Dodaj nowy sprzęt:"), eqClubId, eqName, eqStatus, eqDate, addEqBtn);

        // --- 4. Moduł Treningowy ---
        VBox trainingBox = new VBox(5);
        trainingBox.setStyle("-fx-border-color: green; -fx-padding: 10;");
        Button getExBtn = new Button("Pokaż ćwiczenia");
        Button getPlansBtn = new Button("Pokaż plany");
        TextArea trainDisplay = new TextArea();
        trainDisplay.setPrefHeight(100);
        trainingBox.getChildren().addAll(new Label("Moduł Treningowy:"), getExBtn, getPlansBtn, trainDisplay);

        // ==========================================
        // OBSŁUGA ZDARZEŃ (KLIKNIĘCIA)
        // ==========================================

        loginBtn.setOnAction(e -> {
            String resp = networkClient.sendRequest("LOGIN;" + emailField.getText() + ";" + passField.getText());
            System.out.println("Odpowiedź logowania: " + resp);
            String[] t = resp.split(";");
            if (t.length >= 4 && "LOGIN_OK".equals(t[0])) {
                currentUserId = Integer.parseInt(t[3]);
                statusLabel.setText("Zalogowano pomyślnie: " + t[1]);
                statusLabel.setStyle("-fx-text-fill: green;");
            } else {
                statusLabel.setText("Błąd: " + (t.length > 1 ? t[1] : "Błędne dane"));
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        regBtn.setOnAction(e -> {
            String req = String.format("REGISTER;%s;%s;%s;%s;%s;%s", 
                regPesel.getText(), regFn.getText(), regLn.getText(), regEmail.getText(), regPass.getText(), regRole.getValue());
            String resp = networkClient.sendRequest(req);
            System.out.println("Odpowiedź rejestracji: " + resp);
            
            Alert alert = new Alert(resp.startsWith("REGISTER_OK") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setContentText(resp.contains(";") ? resp.split(";")[1] : resp);
            alert.showAndWait();
        });

        getClubsBtn.setOnAction(e -> {
            String resp = networkClient.sendRequest("GET_CLUBS");
            System.out.println("Serwer zwrócił (Kluby): " + resp);
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

        addClubBtn.setOnAction(e -> {
            String req = String.format("ADD_CLUB;%s;%s;%s",
                clubName.getText(), clubAddress.getText(), clubHours.getText());
            String resp = networkClient.sendRequest(req);
            System.out.println("Odpowiedź dodawania klubu: " + (resp == null ? "poprawnie dodano" : resp));

            Alert alert = new Alert(resp != null && resp.startsWith("ADD_CLUB_OK") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setContentText(resp != null && resp.contains(";") ? resp.split(";")[1] : (resp == null ? "Błąd" : resp));
            alert.showAndWait();

            if (resp != null && resp.startsWith("ADD_CLUB_OK")) {
                getClubsBtn.fire();
            }
        });

        getEqBtn.setOnAction(e -> {
            String resp = networkClient.sendRequest("GET_EQUIPMENT");
            System.out.println("Serwer zwrócił (Sprzęt): " + resp);
            eqDisplay.clear();
            
            if (resp != null && resp.startsWith("EQUIPMENT_OK")) {
                String[] tokens = resp.split(";");
                if (tokens.length <= 1) {
                    eqDisplay.setText("Baza sprzętu jest pusta.");
                } else {
                    for (int i = 1; i < tokens.length; i++) {
                        String[] data = tokens[i].split(",");
                        if (data.length >= 3) {
                            eqDisplay.appendText("ID: " + data[0] + " | Nazwa: " + data[1] + " | Status: " + data[2] + "\n");
                        } else {
                            eqDisplay.appendText("Dane: " + tokens[i] + "\n");
                        }
                    }
                }
            } else {
                eqDisplay.setText("Błąd serwera: " + resp);
            }
        });

        addEqBtn.setOnAction(e -> {
            String dateStr = eqDate.getValue() != null ? eqDate.getValue().toString() : "";
            String req = String.format("ADD_EQUIPMENT;%s;%s;%s;%s", 
                eqClubId.getText(), eqName.getText(), eqStatus.getValue(), dateStr);
            String resp = networkClient.sendRequest(req);
            System.out.println("Odpowiedź dodawania sprzętu: " + (resp == null ? "poprawnie dodano" : resp));

            if (resp != null && resp.startsWith("ADD_EQUIPMENT_OK")) {
                getEqBtn.fire();
            }
        });

        getExBtn.setOnAction(e -> {
            String resp = networkClient.sendRequest("GET_EXERCISES");
            System.out.println("Serwer zwrócił (Ćwiczenia): " + resp);
            trainDisplay.clear();
            
            if (resp != null && resp.startsWith("EXERCISES_OK")) {
                String[] tokens = resp.split(";");
                if (tokens.length <= 1) {
                    trainDisplay.setText("Baza ćwiczeń jest pusta.");
                } else {
                    for (int i = 1; i < tokens.length; i++) {
                        String[] data = tokens[i].split(",");
                        if (data.length >= 2) {
                            trainDisplay.appendText("ID: " + data[0] + " | Ćwiczenie: " + data[1] + "\n");
                        } else {
                            trainDisplay.appendText("Dane: " + tokens[i] + "\n");
                        }
                    }
                }
            } else {
                trainDisplay.setText("Błąd serwera: " + resp);
            }
        });

        getPlansBtn.setOnAction(e -> {
            if (currentUserId == -1) {
                trainDisplay.setText("Musisz się najpierw zalogować, aby zobaczyć swoje plany!");
                return;
            }
            
            String resp = networkClient.sendRequest("GET_PLANS;" + currentUserId);
            System.out.println("Serwer zwrócił (Plany): " + resp);
            trainDisplay.clear();
            
            if (resp != null && resp.startsWith("PLANS_OK")) {
                String[] tokens = resp.split(";");
                if (tokens.length <= 1) {
                    trainDisplay.setText("Nie masz jeszcze żadnych planów treningowych.");
                } else {
                    for (int i = 1; i < tokens.length; i++) {
                        String[] data = tokens[i].split(",");
                        if (data.length >= 2) {
                            trainDisplay.appendText("Plan ID: " + data[0] + " | Cel: " + data[1] + "\n");
                        } else {
                            trainDisplay.appendText("Dane: " + tokens[i] + "\n");
                        }
                    }
                }
            } else {
                trainDisplay.setText("Błąd serwera: " + resp);
            }
        });

        VBox root = new VBox(15, loginGrid, regBox, clubBox, dataBox, trainingBox);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        
        stage.setScene(new Scene(scroll, 500, 800));
        stage.show();
    }

    @Override
    public void stop() {
        if (networkClient != null) networkClient.close();
    }

    public static void main(String[] args) { launch(args); }
}