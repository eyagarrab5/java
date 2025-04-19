package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainHome extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SignUp.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Sign Up");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
