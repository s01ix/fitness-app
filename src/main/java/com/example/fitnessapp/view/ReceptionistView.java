package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReceptionistView extends VBox {
    public ReceptionistView(NetworkClient networkClient, int currentUserId) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        getChildren().add(new Label("RECEPTIONIST"));
    }
}

