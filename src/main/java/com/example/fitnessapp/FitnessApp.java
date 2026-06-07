package com.example.fitnessapp;

import com.example.fitnessapp.view.*;
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
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        networkClient = new NetworkClient();
        stage.setTitle("System Fitness");

        showLoginScreen();
    }

    private void showLoginScreen() {
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
        TextField regRole = new TextField(); regRole.setPromptText("Rola");
        Button regBtn = new Button("Zarejestruj");
        regBox.getChildren().addAll(new Label("Rejestracja nowego konta:"), regPesel, regFn, regLn, regEmail, regPass, regBtn, regRole);

        // ==========================================
        // OBSŁUGA ZDARZEŃ (KLIKNIĘCIA)
        // ==========================================

        loginBtn.setOnAction(e -> {
            String resp = networkClient.sendRequest("LOGIN;" + emailField.getText() + ";" + passField.getText());
            System.out.println("Odpowiedź logowania: " + resp);
            if (resp != null && resp.startsWith("LOGIN_OK")) {
                String[] t = resp.split(";");
                if (t.length >= 4) {
                    currentUserId = Integer.parseInt(t[3].trim());
                    String role = t[2].trim();
                    showRoleScreen(role);
                }
            } else {
                statusLabel.setText("Błąd logowania");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        regBtn.setOnAction(e -> {
            String req = String.format("REGISTER;%s;%s;%s;%s;%s;%s",
                regPesel.getText(), regFn.getText(), regLn.getText(), regEmail.getText(), regPass.getText(), regRole.getText());
            String resp = networkClient.sendRequest(req);
            System.out.println("Odpowiedź rejestracji: " + resp);

            Alert alert = new Alert(resp != null && resp.startsWith("REGISTER_OK") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setContentText(resp != null && resp.contains(";") ? resp.split(";")[1] : (resp == null ? "Błąd" : resp));
            alert.showAndWait();
        });

        VBox root = new VBox(20, loginGrid, regBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    private void showRoleScreen(String role) {
        VBox roleView;
        switch(role.toUpperCase()) {
            case "CLIENT": roleView = new ClientView(networkClient, currentUserId); break;
            case "TRAINER": roleView = new TrainerView(networkClient, currentUserId); break;
            case "RECEPTIONIST": roleView = new ReceptionistView(networkClient, currentUserId); break;
            case "MANAGER": roleView = new ManagerView(networkClient, currentUserId); break;
            case "ADMIN": roleView = new AdminView(networkClient, currentUserId); break;
            default: roleView = new VBox(new Label("Nieznana rola: " + role));
        }

        Button logoutBtn = new Button("Wyloguj");
        logoutBtn.setOnAction(e -> {
            currentUserId = -1;
            showLoginScreen();
        });
        roleView.getChildren().add(logoutBtn);
        roleView.setAlignment(Pos.CENTER);

        primaryStage.setScene(new Scene(roleView, 800, 600));
    }

    @Override
    public void stop() {
        if (networkClient != null) networkClient.close();
    }

    public static void main(String[] args) { launch(args); }
}