module com.project.morpion {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.project.morpion to javafx.fxml;
    exports com.project.morpion;
    exports com.project.morpion.controller;
    opens com.project.morpion.controller to javafx.fxml;
}