package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import com.example.fitnessapp.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistView extends VBox {
    private NetworkClient networkClient;
    private int currentUserId;

    private TabPane tabPane;

    private TextField searchClientField;
    private Button searchClientBtn;
    private Label clientInfoLabel;
    private int selectedClientId = -1;

    private ComboBox<String> passTypeCombo;
    private List<PassType> passTypesList = new ArrayList<>();
    private Label priceLabel;
    private ComboBox<String> paymentMethodCombo;
    private Button sellPassBtn;

    private TextArea clientPassesArea;
    private Button refreshPassesBtn;

    private TableView<GroupClassDisplay> classesTable;
    private ObservableList<GroupClassDisplay> classesList = FXCollections.observableArrayList();
    private Button refreshClassesBtn;

    private TextField resClientSearchField;
    private Button resClientSearchBtn;
    private Label resClientInfoLabel;
    private int resSelectedClientId = -1;

    private Button createReservationBtn;
    private TextArea clientReservationsArea;
    private Button cancelReservationBtn;
    private TextField cancelResIdField;

    public ReceptionistView(NetworkClient networkClient, int currentUserId) {
        this.networkClient = networkClient;
        this.currentUserId = currentUserId;

        Label titleLabel = new Label("Panel Recepcjonisty");

        tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createPassSalesTab(),
                createReservationsTab()
        );

        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.getChildren().addAll(titleLabel, tabPane);

        loadPassTypes();
    }

    private Tab createPassSalesTab() {
        Tab tab = new Tab("Sprzedaż karnetów");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label searchLabel = new Label("1. Wyszukaj klienta:");

        searchClientField = new TextField();
        searchClientField.setPromptText("Email lub PESEL klienta");
        searchClientField.setPrefWidth(250);

        searchClientBtn = new Button("Wyszukaj");
        searchClientBtn.setOnAction(e -> searchClient());

        HBox searchBox = new HBox(10, searchClientField, searchClientBtn);

        clientInfoLabel = new Label("Nie wybrano klienta");

        Label passTypeLabel = new Label("2. Wybierz typ karnetu:");

        passTypeCombo = new ComboBox<>();
        passTypeCombo.setPromptText("Wybierz karnet");
        passTypeCombo.setPrefWidth(250);
        passTypeCombo.setOnAction(e -> updatePrice());

        priceLabel = new Label("Cena: -");

        Label paymentLabel = new Label("3. Metoda płatności:");

        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("CASH", "CARD", "TRANSFER", "BLIK");
        paymentMethodCombo.setValue("CARD");
        paymentMethodCombo.setPrefWidth(200);

        sellPassBtn = new Button("SPRZEDAJ KARNET");
        sellPassBtn.setOnAction(e -> handleSellPass());

        Label historyLabel = new Label("Aktywne karnety klienta:");

        clientPassesArea = new TextArea();
        clientPassesArea.setPrefHeight(150);
        clientPassesArea.setEditable(false);

        refreshPassesBtn = new Button("Odśwież karnety");
        refreshPassesBtn.setOnAction(e -> refreshClientPasses());

        content.getChildren().addAll(
                searchLabel, searchBox, clientInfoLabel,
                new Separator(),
                passTypeLabel, passTypeCombo, priceLabel,
                new Separator(),
                paymentLabel, paymentMethodCombo,
                new Separator(),
                sellPassBtn,
                new Separator(),
                historyLabel, clientPassesArea, refreshPassesBtn
        );

        tab.setContent(content);
        return tab;
    }

    private Tab createReservationsTab() {
        Tab tab = new Tab("Rezerwacje zajęć grupowych");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label scheduleLabel = new Label("Harmonogram zajęć grupowych:");

        classesTable = new TableView<>();
        classesTable.setItems(classesList);
        classesTable.setPrefHeight(250);

        TableColumn<GroupClassDisplay, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<GroupClassDisplay, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(150);

        TableColumn<GroupClassDisplay, String> timeCol = new TableColumn<>("Data i godzina");
        timeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getScheduleTime()));
        timeCol.setPrefWidth(150);

        TableColumn<GroupClassDisplay, String> capacityCol = new TableColumn<>("Miejsca");
        capacityCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCapacityInfo()));
        capacityCol.setPrefWidth(100);

        TableColumn<GroupClassDisplay, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(100);

        classesTable.getColumns().addAll(idCol, nameCol, timeCol, capacityCol, statusCol);

        refreshClassesBtn = new Button("Odśwież harmonogram");
        refreshClassesBtn.setOnAction(e -> loadGroupClasses());

        Label createResLabel = new Label("Tworzenie rezerwacji:");

        resClientSearchField = new TextField();
        resClientSearchField.setPromptText("Email lub PESEL klienta");
        resClientSearchField.setPrefWidth(250);

        resClientSearchBtn = new Button("Wyszukaj");
        resClientSearchBtn.setOnAction(e -> searchClientForReservation());

        HBox resSearchBox = new HBox(10, resClientSearchField, resClientSearchBtn);

        resClientInfoLabel = new Label("Nie wybrano klienta");

        createReservationBtn = new Button("Zarezerwuj wybrane zajęcia");
        createReservationBtn.setOnAction(e -> handleCreateReservation());

        Label cancelLabel = new Label("Anulowanie rezerwacji:");

        cancelResIdField = new TextField();
        cancelResIdField.setPromptText("ID rezerwacji do anulowania");
        cancelResIdField.setPrefWidth(200);

        cancelReservationBtn = new Button("Anuluj rezerwację");
        cancelReservationBtn.setOnAction(e -> handleCancelReservation());

        HBox cancelBox = new HBox(10, cancelResIdField, cancelReservationBtn);

        Label clientResLabel = new Label("Rezerwacje wybranego klienta:");

        clientReservationsArea = new TextArea();
        clientReservationsArea.setPrefHeight(120);
        clientReservationsArea.setEditable(false);

        content.getChildren().addAll(
                scheduleLabel, classesTable, refreshClassesBtn,
                new Separator(),
                createResLabel, resSearchBox, resClientInfoLabel, createReservationBtn,
                new Separator(),
                cancelLabel, cancelBox,
                new Separator(),
                clientResLabel, clientReservationsArea
        );

        loadGroupClasses();

        tab.setContent(content);
        return tab;
    }

    private void loadPassTypes() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_PASS_TYPES");
            Platform.runLater(() -> {
                passTypesList.clear();
                passTypeCombo.getItems().clear();

                if (resp != null && resp.startsWith("PASS_TYPES_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] parts = tokens[i].split(",");
                        if (parts.length >= 3) {
                            int id = Integer.parseInt(parts[0]);
                            String name = parts[1];
                            BigDecimal price = new BigDecimal(parts[2]);

                            PassType pt = new PassType(id, name, price);
                            passTypesList.add(pt);
                            passTypeCombo.getItems().add(id + " - " + name + " (" + price + " zł)");
                        }
                    }
                }
            });
        }).start();
    }

    private void searchClient() {
        String searchTerm = searchClientField.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert("Błąd", "Podaj email lub PESEL klienta", Alert.AlertType.WARNING);
            return;
        }

        new Thread(() -> {
            String resp = networkClient.sendRequest("SEARCH_CLIENT;" + searchTerm);
            Platform.runLater(() -> {
                if (resp != null && resp.startsWith("CLIENT_FOUND")) {
                    String[] tokens = resp.split(";");
                    if (tokens.length >= 5) {
                        selectedClientId = Integer.parseInt(tokens[1]);
                        String name = tokens[2] + " " + tokens[3];
                        String email = tokens[4];
                        clientInfoLabel.setText("✓ Klient: " + name + " (" + email + ")");
                        refreshClientPasses();
                    }
                } else {
                    selectedClientId = -1;
                    clientInfoLabel.setText("✗ Nie znaleziono klienta");
                    clientPassesArea.clear();
                }
            });
        }).start();
    }

    private void updatePrice() {
        String selected = passTypeCombo.getValue();
        if (selected != null && !selected.isEmpty()) {
            int id = Integer.parseInt(selected.split(" - ")[0]);
            PassType pt = passTypesList.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);
            if (pt != null) {
                priceLabel.setText("Cena: " + pt.getBasePrice() + " zł");
            }
        }
    }

    private void handleSellPass() {
        if (selectedClientId == -1) {
            showAlert("Błąd", "Wybierz klienta", Alert.AlertType.WARNING);
            return;
        }

        String selectedPass = passTypeCombo.getValue();
        if (selectedPass == null || selectedPass.isEmpty()) {
            showAlert("Błąd", "Wybierz typ karnetu", Alert.AlertType.WARNING);
            return;
        }

        int passTypeId = Integer.parseInt(selectedPass.split(" - ")[0]);
        String paymentMethod = paymentMethodCombo.getValue();

        new Thread(() -> {
            String req = String.format("SELL_PASS;%d;%d;%s", selectedClientId, passTypeId, paymentMethod);
            String resp = networkClient.sendRequest(req);

            Platform.runLater(() -> {
                if (resp != null && resp.startsWith("SELL_PASS_OK")) {
                    showAlert("Sukces", "Karnet został sprzedany!", Alert.AlertType.INFORMATION);
                    refreshClientPasses();
                } else {
                    showAlert("Błąd", resp != null ? resp : "Nieznany błąd", Alert.AlertType.ERROR);
                }
            });
        }).start();
    }

    private void refreshClientPasses() {
        if (selectedClientId == -1) return;

        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_CLIENT_PASSES;" + selectedClientId);
            Platform.runLater(() -> {
                clientPassesArea.clear();
                if (resp != null && resp.startsWith("PASSES_OK")) {
                    String[] tokens = resp.split(";");
                    if (tokens.length <= 1) {
                        clientPassesArea.setText("Brak aktywnych karnetów");
                    } else {
                        for (int i = 1; i < tokens.length; i++) {
                            clientPassesArea.appendText(tokens[i] + "\n");
                        }
                    }
                }
            });
        }).start();
    }

    private void loadGroupClasses() {
        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_GROUP_CLASSES");
            Platform.runLater(() -> {
                classesList.clear();
                if (resp != null && resp.startsWith("CLASSES_OK")) {
                    String[] tokens = resp.split(";");
                    for (int i = 1; i < tokens.length; i++) {
                        String[] parts = tokens[i].split(",");
                        if (parts.length >= 6) {
                            classesList.add(new GroupClassDisplay(
                                    Integer.parseInt(parts[0]),
                                    parts[1],
                                    parts[2],
                                    Integer.parseInt(parts[3]),
                                    Integer.parseInt(parts[4]),
                                    parts[5]
                            ));
                        }
                    }
                }
            });
        }).start();
    }

    private void searchClientForReservation() {
        String searchTerm = resClientSearchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert("Błąd", "Podaj email lub PESEL klienta", Alert.AlertType.WARNING);
            return;
        }

        new Thread(() -> {
            String resp = networkClient.sendRequest("SEARCH_CLIENT;" + searchTerm);
            Platform.runLater(() -> {
                if (resp != null && resp.startsWith("CLIENT_FOUND")) {
                    String[] tokens = resp.split(";");
                    if (tokens.length >= 5) {
                        resSelectedClientId = Integer.parseInt(tokens[1]);
                        String name = tokens[2] + " " + tokens[3];
                        String email = tokens[4];
                        resClientInfoLabel.setText("✓ Klient: " + name + " (" + email + ")");
                        loadClientReservations();
                    }
                } else {
                    resSelectedClientId = -1;
                    resClientInfoLabel.setText("✗ Nie znaleziono klienta");
                    clientReservationsArea.clear();
                }
            });
        }).start();
    }

    private void handleCreateReservation() {
        if (resSelectedClientId == -1) {
            showAlert("Błąd", "Wybierz klienta", Alert.AlertType.WARNING);
            return;
        }

        GroupClassDisplay selected = classesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Błąd", "Wybierz zajęcia z harmonogramu", Alert.AlertType.WARNING);
            return;
        }

        new Thread(() -> {
            String req = String.format("CREATE_RESERVATION;%d;%d", resSelectedClientId, selected.getId());
            String resp = networkClient.sendRequest(req);

            Platform.runLater(() -> {
                if (resp != null && resp.startsWith("RESERVATION_OK")) {
                    showAlert("Sukces", "Rezerwacja została utworzona!", Alert.AlertType.INFORMATION);
                    loadGroupClasses();
                    loadClientReservations();
                } else {
                    showAlert("Błąd", resp != null ? resp : "Nieznany błąd", Alert.AlertType.ERROR);
                }
            });
        }).start();
    }

    private void handleCancelReservation() {
        String resIdStr = cancelResIdField.getText().trim();
        if (resIdStr.isEmpty()) {
            showAlert("Błąd", "Podaj ID rezerwacji", Alert.AlertType.WARNING);
            return;
        }

        try {
            int resId = Integer.parseInt(resIdStr);

            new Thread(() -> {
                String resp = networkClient.sendRequest("CANCEL_RESERVATION;" + resId);
                Platform.runLater(() -> {
                    if (resp != null && resp.startsWith("CANCEL_OK")) {
                        showAlert("Sukces", "Rezerwacja została anulowana", Alert.AlertType.INFORMATION);
                        cancelResIdField.clear();
                        loadGroupClasses();
                        if (resSelectedClientId != -1) {
                            loadClientReservations();
                        }
                    } else {
                        showAlert("Błąd", resp != null ? resp : "Nieznany błąd", Alert.AlertType.ERROR);
                    }
                });
            }).start();
        } catch (NumberFormatException e) {
            showAlert("Błąd", "ID musi być liczbą", Alert.AlertType.ERROR);
        }
    }

    private void loadClientReservations() {
        if (resSelectedClientId == -1) return;

        new Thread(() -> {
            String resp = networkClient.sendRequest("GET_CLIENT_RESERVATIONS;" + resSelectedClientId);
            Platform.runLater(() -> {
                clientReservationsArea.clear();
                if (resp != null && resp.startsWith("RESERVATIONS_OK")) {
                    String[] tokens = resp.split(";");
                    if (tokens.length <= 1) {
                        clientReservationsArea.setText("Brak rezerwacji");
                    } else {
                        for (int i = 1; i < tokens.length; i++) {
                            clientReservationsArea.appendText(tokens[i] + "\n");
                        }
                    }
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

    public static class GroupClassDisplay {
        private final int id;
        private final String name;
        private final String scheduleTime;
        private final int capacity;
        private final int bookedCount;
        private final String status;

        public GroupClassDisplay(int id, String name, String scheduleTime,
                                 int capacity, int bookedCount, String status) {
            this.id = id;
            this.name = name;
            this.scheduleTime = scheduleTime;
            this.capacity = capacity;
            this.bookedCount = bookedCount;
            this.status = status;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getScheduleTime() { return scheduleTime; }
        public String getCapacityInfo() { return bookedCount + "/" + capacity; }
        public String getStatus() { return status; }
    }
}