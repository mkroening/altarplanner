package org.altarplanner.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.altarplanner.core.domain.Config;

import java.io.IOException;
import java.util.ResourceBundle;

public class Launcher extends Application implements ConfigAware {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.app.locale.locale");

    private static Stage primaryStage;
    public static void loadParent(String location, Config config) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource(location), RESOURCE_BUNDLE);
        Parent root = loader.load();
        Object controller = loader.getController();

        if (controller instanceof ConfigAware) {
            if (config == null)
                config = Config.load();
            ((ConfigAware) controller).initConfig(config);
        }

        String name = controller.getClass().getSimpleName();
        String key = name.substring(0, 1).toLowerCase() + name.substring(1);
        String title = RESOURCE_BUNDLE.getString(key);

        primaryStage.setTitle("AltarPlanner - " + title);
        primaryStage.setMinHeight(root.minHeight(-1));
        primaryStage.setMinWidth(root.minWidth(-1));
        primaryStage.setHeight(root.prefHeight(-1));
        primaryStage.setWidth(root.prefWidth(-1));
        primaryStage.getScene().setRoot(root);
    }

    private Config config;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Launcher.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("launcher.fxml"), RESOURCE_BUNDLE);
        primaryStage.setTitle("AltarPlanner");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("org/altarplanner/app/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void initConfig(Config config) {
        this.config = config;
    }

    public void loadServiceTypeEditor() throws IOException {
        loadParent("config/serviceTypeEditor.fxml", config);
    }

    public void loadRegularMassEditor() throws IOException {
        loadParent("config/regularMassEditor.fxml", config);
    }

    public void loadServerEditor() throws IOException {
        loadParent("config/serverEditor.fxml", config);
    }

}
