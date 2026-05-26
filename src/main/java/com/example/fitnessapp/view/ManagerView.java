package com.example.fitnessapp.view;

import com.example.fitnessapp.NetworkClient;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ManagerView extends VBox {
    public ManagerView(NetworkClient networkClient, int currentUserId) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        getChildren().add(new Label("MANAGER"));
    }
}

