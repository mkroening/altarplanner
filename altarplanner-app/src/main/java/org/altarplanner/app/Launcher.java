package org.altarplanner.app;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.altarplanner.core.domain.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ResourceBundle;

public class Launcher extends Application {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.app.locale.locale");

    private Config config;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("launcher.fxml"), RESOURCE_BUNDLE);
        primaryStage.setTitle("AltarPlanner");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    public void initialize() {
        try {
            this.config = Config.load(new File("config.xml"));
        } catch (FileNotFoundException e) {
            System.out.println("File");
            this.config = new Config();
        }
    }

}
