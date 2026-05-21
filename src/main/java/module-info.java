module com.example.fitnessapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics; // Dodano, aby rozwiązać problem z Stage
    requires javafx.base;     // Dodano, aby zapewnić dostęp do właściwości JavaFX

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens com.example.fitnessapp to javafx.fxml;
    exports com.example.fitnessapp;
}