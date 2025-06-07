package com.username.moviesapp; // Your actual package name

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // CORRECTED: Added a leading slash to find main-view.fxml at the root of resources
        URL fxmlLocation = getClass().getResource("/main-view.fxml"); // <-- The important change is here!
        if (fxmlLocation == null) {
            System.err.println("Error: main-view.fxml not found. Please ensure it's in src/main/resources/main-view.fxml.");
            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Get the controller and set the primary stage
        MainController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);

        stage.setTitle("Movie Database App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}