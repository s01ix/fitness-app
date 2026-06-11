package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReceptionistView extends VBox {
    private final NetworkClient networkClient;
    private final int currentUserId;

    private int selectedClientId = -1;
    private Label clientInfoLabel;
    private TextField searchClientField;
    private TextArea clientPassesArea;

    public ReceptionistView(NetworkClient networkClient, int currentUserId) {
        this.networkClient = networkClient;
        this.currentUserId = currentUserId;

        Label titleLabel = new Label("PANEL RECEPCJI - SYSTEM OBSŁUGI KLUBU");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                buildCheckInTab(),
                buildSalesTab(),
                buildReservationsTab(),
                buildHelpdeskTab()
        );

        this.setPadding(new Insets(15));
        this.setSpacing(15);
        this.getChildren().addAll(titleLabel, tabPane);
    }

    private Tab buildCheckInTab() {
        Tab tab = new Tab("Weryfikacja & Rejestracja");
        HBox mainLayout = new HBox(30); mainLayout.setPadding(new Insets(20));

        VBox checkInBox = new VBox(15); checkInBox.setPrefWidth(350);
        Label lbl1 = new Label("Weryfikacja wejścia (Check-in):"); lbl1.setStyle("-fx-font-weight: bold;");
        TextField checkInField = new TextField(); checkInField.setPromptText("ID Klienta lub PESEL");
        Button verifyBtn = new Button("Skanuj / Weryfikuj wejście");
        verifyBtn.setStyle("-fx-background-color: #2b6cb0; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 350px;");
        
        Label accessStatusLabel = new Label("Oczekiwanie na skan...");
        accessStatusLabel.setPrefSize(350, 100); accessStatusLabel.setAlignment(Pos.CENTER);
        accessStatusLabel.setStyle("-fx-background-color: #edf2f7; -fx-font-size: 16px; -fx-font-weight: bold; -fx-border-color: #cbd5e1; -fx-border-width: 2px;");

        verifyBtn.setOnAction(e -> {
            new Thread(() -> {
                String findResp = networkClient.sendRequest("SEARCH_CLIENT;" + checkInField.getText().trim());
                if (findResp != null && findResp.startsWith("CLIENT_FOUND")) {
                    String id = findResp.split(";")[1];
                    String accessResp = networkClient.sendRequest("VERIFY_ENTRY;" + id);
                    Platform.runLater(() -> {
                        if (accessResp != null && accessResp.startsWith("VERIFY_GRANTED")) {
                            accessStatusLabel.setText("Dostęp Przyznany!\nKarnet AKTYWNY");
                            accessStatusLabel.setStyle("-fx-background-color: #c6f6d5; -fx-text-fill: #276749; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: #38a169; -fx-border-width: 3px;");
                        } else {
                            accessStatusLabel.setText("Odmowa Dostępu!\nBrak aktywnego karnetu");
                            accessStatusLabel.setStyle("-fx-background-color: #fed7d7; -fx-text-fill: #c53030; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: #e53e3e; -fx-border-width: 3px;");
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        accessStatusLabel.setText("BŁĄD: Taki klient nie istnieje w bazie");
                        accessStatusLabel.setStyle("-fx-background-color: #fed7d7; -fx-text-fill: #c53030; -fx-font-size: 16px; -fx-font-weight: bold;");
                    });
                }
            }).start();
        });
        checkInBox.getChildren().addAll(lbl1, checkInField, verifyBtn, accessStatusLabel);

        VBox regBox = new VBox(10); regBox.setPrefWidth(350);
        Label lbl2 = new Label("Szybka Rejestracja Nowego Klienta:"); lbl2.setStyle("-fx-font-weight: bold;");
        TextField rPesel = new TextField(); rPesel.setPromptText("PESEL (11 cyfr)");
        TextField rName = new TextField(); rName.setPromptText("Imię");
        TextField rLast = new TextField(); rLast.setPromptText("Nazwisko");
        TextField rEmail = new TextField(); rEmail.setPromptText("Email");
        PasswordField rPass = new PasswordField(); rPass.setPromptText("Tymczasowe Hasło");
        Button regBtn = new Button("Zarejestruj Klienta");
        regBtn.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 350px;");

        regBtn.setOnAction(e -> {
            new Thread(() -> {
                String req = String.format("REGISTER;%s;%s;%s;%s;%s;CLIENT", rPesel.getText(), rName.getText(), rLast.getText(), rEmail.getText(), rPass.getText());
                String resp = networkClient.sendRequest(req);
                Platform.runLater(() -> {
                    showAlert("Rejestracja", resp);
                    if(resp != null && resp.startsWith("REGISTER_OK")) { rPesel.clear(); rName.clear(); rLast.clear(); rEmail.clear(); rPass.clear(); }
                });
            }).start();
        });
        regBox.getChildren().addAll(lbl2, rPesel, rName, rLast, rEmail, rPass, regBtn);

        mainLayout.getChildren().addAll(checkInBox, new Separator(javafx.geometry.Orientation.VERTICAL), regBox);
        tab.setContent(mainLayout);
        return tab;
    }

    private Tab buildSalesTab() {
        Tab tab = new Tab("Sprzedaż Karnetów");
        VBox content = new VBox(15); content.setPadding(new Insets(20));

        searchClientField = new TextField(); searchClientField.setPromptText("Email lub PESEL klienta");
        Button searchClientBtn = new Button("Znajdź w systemie");
        clientInfoLabel = new Label("Brak wybranego klienta"); clientInfoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2b6cb0;");
        
        searchClientBtn.setOnAction(e -> {
            new Thread(() -> {
                String resp = networkClient.sendRequest("SEARCH_CLIENT;" + searchClientField.getText().trim());
                Platform.runLater(() -> {
                    if (resp != null && resp.startsWith("CLIENT_FOUND")) {
                        String[] t = resp.split(";");
                        selectedClientId = Integer.parseInt(t[1]);
                        clientInfoLabel.setText("Aktywny profil: " + t[2] + " " + t[3] + " (" + t[4] + ")");
                        refreshClientPasses();
                    } else {
                        selectedClientId = -1; 
                        clientInfoLabel.setText("Nie znaleziono klienta.");
                        clientPassesArea.clear();
                    }
                });
            }).start();
        });

        HBox topBox = new HBox(15, new Label("Wyszukaj klienta:"), searchClientField, searchClientBtn, clientInfoLabel);

        ComboBox<String> passTypeCombo = new ComboBox<>(); passTypeCombo.setPromptText("Wybierz karnet z bazy...");
        passTypeCombo.setPrefWidth(250);
        ComboBox<String> paymentMethodCombo = new ComboBox<>(); paymentMethodCombo.getItems().addAll("CASH", "CARD", "BLIK", "TRANSFER"); paymentMethodCombo.setValue("CARD");
        Button sellBtn = new Button("Finalizuj Sprzedaż"); sellBtn.setStyle("-fx-background-color: #d69e2e; -fx-text-fill: white; -fx-font-weight: bold;");

        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_PASS_TYPES");
            Platform.runLater(() -> {
                if (resp != null && resp.startsWith("PASS_TYPES_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] p = tokens[i].split(",");
                        if(p.length >= 3) {
                            passTypeCombo.getItems().add(p[0] + " - " + p[1] + " (" + p[2] + " PLN)");
                        }
                    }
                }
            });
        }).start();

        sellBtn.setOnAction(e -> {
            if (selectedClientId != -1 && passTypeCombo.getValue() != null) {
                new Thread(() -> {
                    String passId = passTypeCombo.getValue().split(" - ")[0]; // Pobiera bezpiecznie tylko ID
                    String req = String.format("SELL_PASS;%d;%s;%s", selectedClientId, passId, paymentMethodCombo.getValue());
                    String resp = networkClient.sendRequest(req);
                    Platform.runLater(() -> { showAlert("Transakcja", resp); refreshClientPasses(); });
                }).start();
            } else { 
                showAlert("Błąd", "Najpierw wyszukaj klienta i wybierz rodzaj karnetu z listy."); 
            }
        });

        HBox sellBox = new HBox(15, passTypeCombo, paymentMethodCombo, sellBtn);

        clientPassesArea = new TextArea(); clientPassesArea.setPrefHeight(150); clientPassesArea.setEditable(false);
        content.getChildren().addAll(topBox, new Separator(), new Label("Nowy zakup:"), sellBox, new Separator(), new Label("Historia aktywnych karnetów wybranego klienta:"), clientPassesArea);
        tab.setContent(content); return tab;
    }

    private Tab buildReservationsTab() {
        Tab tab = new Tab("Zajęcia i Rezerwacje");
        VBox content = new VBox(15); content.setPadding(new Insets(20));

        TextArea scheduleArea = new TextArea(); scheduleArea.setPrefHeight(150); scheduleArea.setEditable(false);
        Button loadScheduleBtn = new Button("Pobierz Aktualny Harmonogram Zajęć");
        loadScheduleBtn.setOnAction(e -> {
            new Thread(() -> {
                String resp = networkClient.sendRequest("GET_GROUP_CLASSES");
                Platform.runLater(() -> {
                    scheduleArea.clear();
                    if(resp != null && resp.startsWith("CLASSES_OK")) {
                        String[] tokens = resp.split(";");
                        if(tokens.length == 1) scheduleArea.setText("Brak zajęć.");
                        for(int i = 1; i < tokens.length; i++) scheduleArea.appendText("Zajęcia: " + tokens[i] + "\n");
                    } else {
                        showAlert("Błąd", resp);
                    }
                });
            }).start();
        });

        HBox actionBox = new HBox(15);
        TextField classIdField = new TextField(); classIdField.setPromptText("ID Zajęć (do zapisu)");
        Button enrollBtn = new Button("Zapisz wczytanego klienta");
        enrollBtn.setOnAction(e -> {
            if(selectedClientId != -1 && !classIdField.getText().isEmpty()) {
                new Thread(() -> {
                    String resp = networkClient.sendRequest("CREATE_RESERVATION;" + selectedClientId + ";" + classIdField.getText().trim());
                    Platform.runLater(() -> showAlert("Rezerwacja", resp));
                }).start();
            } else { showAlert("Wymagane działanie", "Musisz wczytać profil klienta w zakładce Sprzedaż oraz podać poprawne ID zajęć z harmonogramu."); }
        });

        TextField cancelIdField = new TextField(); cancelIdField.setPromptText("ID Rezerwacji (do usunięcia)");
        Button cancelBtn = new Button("Odwołaj rezerwację"); cancelBtn.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelBtn.setOnAction(e -> {
            if(!cancelIdField.getText().isEmpty()) {
                new Thread(() -> {
                    String resp = networkClient.sendRequest("CANCEL_RESERVATION;" + cancelIdField.getText().trim());
                    Platform.runLater(() -> showAlert("Anulowanie", resp));
                }).start();
            }
        });

        actionBox.getChildren().addAll(classIdField, enrollBtn, new Separator(javafx.geometry.Orientation.VERTICAL), cancelIdField, cancelBtn);
        content.getChildren().addAll(loadScheduleBtn, scheduleArea, new Separator(), new Label("Zarządzanie miejscami:"), actionBox);
        tab.setContent(content); return tab;
    }

    private Tab buildHelpdeskTab() {
        Tab tab = new Tab("Helpdesk & Info");
        HBox content = new HBox(30); content.setPadding(new Insets(20));

        VBox infoBox = new VBox(10); infoBox.setPrefWidth(350);
        Label lbl1 = new Label("Baza wiedzy - Informacje o placówce:"); lbl1.setStyle("-fx-font-weight: bold;");
        TextArea infoArea = new TextArea(); infoArea.setEditable(false); infoArea.setPrefHeight(150);
        Button loadInfoBtn = new Button("Pobierz dane klubu");
        loadInfoBtn.setOnAction(e -> {
            new Thread(() -> {
                String resp = networkClient.sendRequest("GET_CLUB_INFO");
                Platform.runLater(() -> {
                    if(resp != null && resp.startsWith("CLUB_INFO_OK")) {
                        String[] t = resp.split(";");
                        if(t.length >= 4) infoArea.setText("Nazwa: " + t[1] + "\nAdres: " + t[2] + "\nGodziny otwarcia: " + t[3]);
                    } else {
                        showAlert("Informacja", resp);
                    }
                });
            }).start();
        });
        infoBox.getChildren().addAll(lbl1, loadInfoBtn, infoArea);

        VBox complaintBox = new VBox(10); complaintBox.setPrefWidth(350);
        Label lbl2 = new Label("Zgłoszenie problemu technicznego / Usterki:"); lbl2.setStyle("-fx-font-weight: bold;");
        TextField cClientId = new TextField(); cClientId.setPromptText("ID Klienta (jeśli dotyczy)");
        TextArea complaintArea = new TextArea(); complaintArea.setPromptText("Szczegóły usterki (np. zepsuta bieżnia nr 3, brak wody)..."); complaintArea.setPrefHeight(120);
        Button sendCompBtn = new Button("Przekaż do Managera"); sendCompBtn.setStyle("-fx-background-color: #d69e2e; -fx-text-fill: white; -fx-font-weight: bold;");
        sendCompBtn.setOnAction(e -> {
            new Thread(() -> {
                String id = cClientId.getText().isEmpty() ? "0" : cClientId.getText().trim();
                String text = complaintArea.getText().replace(";", ","); // Zabezpieczenie przed zepsuciem komendy
                if(text.isEmpty()) { Platform.runLater(()->showAlert("Błąd", "Wpisz treść zgłoszenia!")); return; }
                
                String resp = networkClient.sendRequest("LOG_COMPLAINT;" + id + ";" + text);
                Platform.runLater(() -> { showAlert("Status zgłoszenia", resp); complaintArea.clear(); cClientId.clear(); });
            }).start();
        });
        complaintBox.getChildren().addAll(lbl2, cClientId, complaintArea, sendCompBtn);

        content.getChildren().addAll(infoBox, new Separator(javafx.geometry.Orientation.VERTICAL), complaintBox);
        tab.setContent(content); return tab;
    }

    private void refreshClientPasses() {
        if(selectedClientId != -1) {
            new Thread(() -> {
                String resp = networkClient.sendRequest("GET_CLIENT_PASSES;" + selectedClientId);
                Platform.runLater(() -> {
                    clientPassesArea.clear();
                    if(resp != null && resp.startsWith("PASSES_OK")) {
                        String[] tokens = resp.split(";");
                        if (tokens.length == 1) clientPassesArea.setText("Klient nie posiada aktywnych karnetów.");
                        for(int i = 1; i < tokens.length; i++) {
                            clientPassesArea.appendText(tokens[i] + "\n");
                        }
                    } else {
                        showAlert("Błąd systemu", resp);
                    }
                });
            }).start();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); 
        alert.setHeaderText(null);
        
        if (msg != null && msg.contains(";")) {
            String[] parts = msg.split(";", 2); // Dzieli string maksymalnie na 2 części, reszta zostaje nietknięta
            alert.setContentText(parts.length > 1 ? parts[1] : parts[0]);
        } else {
            alert.setContentText(msg != null ? msg : "Wystąpił nieznany błąd podczas łączenia z serwerem.");
        }
        
        alert.showAndWait();
    }
}