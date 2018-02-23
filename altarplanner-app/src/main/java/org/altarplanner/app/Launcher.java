package org.altarplanner.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Launcher extends Application {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.app.locale.locale");

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("launcher.fxml"), RESOURCE_BUNDLE);
        primaryStage.setTitle("AltarPlanner");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
