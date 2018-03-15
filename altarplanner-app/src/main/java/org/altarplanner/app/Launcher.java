package org.altarplanner.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.altarplanner.app.config.RegularMassEditor;
import org.altarplanner.app.config.ServerEditor;
import org.altarplanner.app.config.ServiceTypeEditor;
import org.altarplanner.app.planning.DiscreteMassGenerator;
import org.altarplanner.core.domain.Config;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Launcher extends Application {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.app.locale.locale");
    private static Stage primaryStage;

    @SafeVarargs
    public static void loadParent(String location, Consumer<Object>... controllerConsumers) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource(location), RESOURCE_BUNDLE);
        Parent root = loader.load();
        Object controller = loader.getController();

        List.of(controllerConsumers).forEach(controllerConsumer -> controllerConsumer.accept(controller));

        String name = controller.getClass().getSimpleName();
        String key = name.substring(0, 1).toLowerCase() + name.substring(1);
        String title = RESOURCE_BUNDLE.getString(key);

        primaryStage.hide();
        primaryStage.setTitle("AltarPlanner - " + title);
        primaryStage.setMinHeight(root.minHeight(-1));
        primaryStage.setMinWidth(root.minWidth(-1));
        primaryStage.setHeight(root.prefHeight(-1));
        primaryStage.setWidth(root.prefWidth(-1));
        if (primaryStage.getScene() != null) {
            primaryStage.setX(primaryStage.getX());
            primaryStage.setY(primaryStage.getY());
            primaryStage.getScene().setRoot(root);
        } else {
            Scene scene = new Scene(root);
            scene.getStylesheets().add("org/altarplanner/app/style.css");
            primaryStage.setScene(scene);
        }
        primaryStage.show();
    }

    private Config config;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Launcher.primaryStage = primaryStage;
        loadParent("launcher.fxml", launcher -> ((Launcher)launcher).initData(Config.load()));
    }

    public void initData(Config config) {
        this.config = config;
    }

    public void loadServiceTypeEditor() throws IOException {
        loadParent("config/serviceTypeEditor.fxml", serviceTypeEditor -> ((ServiceTypeEditor)serviceTypeEditor).initData(config));
    }

    public void loadRegularMassEditor() throws IOException {
        loadParent("config/regularMassEditor.fxml", regularMassEditor -> ((RegularMassEditor)regularMassEditor).initData(config));
    }

    public void loadServerEditor() throws IOException {
        loadParent("config/serverEditor.fxml", serverEditor -> ((ServerEditor)serverEditor).initData(config));
    }

    public void loadDiscreteMassGenerator() throws IOException {
        loadParent("planning/discreteMassGenerator.fxml", discreteMassGenerator -> ((DiscreteMassGenerator)discreteMassGenerator).initData(config));
    }

}
