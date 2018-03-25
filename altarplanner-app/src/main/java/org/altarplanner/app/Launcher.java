package org.altarplanner.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.altarplanner.app.config.RegularMassEditor;
import org.altarplanner.app.config.ServerEditor;
import org.altarplanner.app.config.ServiceTypeEditor;
import org.altarplanner.app.planning.DiscreteMassEditor;
import org.altarplanner.core.domain.Config;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Launcher extends Application {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.app.locale.locale");
    private static Stage primaryStage;

    @SafeVarargs
    public static void loadParent(String location, boolean inPrimaryStage, Consumer<Object>... controllerConsumers) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource(location), RESOURCE_BUNDLE);
        Parent root = loader.load();
        Object controller = loader.getController();

        List.of(controllerConsumers).forEach(controllerConsumer -> controllerConsumer.accept(controller));

        String name = controller.getClass().getSimpleName();
        String key = name.substring(0, 1).toLowerCase() + name.substring(1);
        String title = RESOURCE_BUNDLE.getString(key);

        Stage stage;
        if (inPrimaryStage) {
            stage = primaryStage;
        } else {
            stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        stage.hide();
        stage.setTitle("AltarPlanner - " + title);
        stage.setMinHeight(root.minHeight(-1));
        stage.setMinWidth(root.minWidth(-1));
        stage.setHeight(root.prefHeight(-1));
        stage.setWidth(root.prefWidth(-1));
        stage.setMaxHeight(root.maxHeight(-1));
        stage.setMaxWidth(root.maxWidth(-1));
        if (stage.getScene() != null) {
            stage.setX(stage.getX());
            stage.setY(stage.getY());
            stage.getScene().setRoot(root);
        } else {
            Scene scene = new Scene(root);
            scene.getStylesheets().add("org/altarplanner/app/style.css");
            stage.setScene(scene);
        }
        stage.show();
    }

    private Config config;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Launcher.primaryStage = primaryStage;
        loadParent("launcher.fxml", true, launcher -> ((Launcher)launcher).initData(Config.load()));
    }

    public void initData(Config config) {
        this.config = config;
    }

    public void loadServiceTypeEditor() throws IOException {
        loadParent("config/serviceTypeEditor.fxml", true, serviceTypeEditor -> ((ServiceTypeEditor)serviceTypeEditor).initData(config));
    }

    public void loadRegularMassEditor() throws IOException {
        loadParent("config/regularMassEditor.fxml", true, regularMassEditor -> ((RegularMassEditor)regularMassEditor).initData(config));
    }

    public void loadServerEditor() throws IOException {
        loadParent("config/serverEditor.fxml", true, serverEditor -> ((ServerEditor)serverEditor).initData(config));
    }

    public void loadDiscreteMassEditor() throws IOException {
        loadParent("planning/discreteMassEditor.fxml", true, discreteMassEditor -> ((DiscreteMassEditor)discreteMassEditor).initData(config));
    }

}
