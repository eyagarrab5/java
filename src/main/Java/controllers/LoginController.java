package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.User;
import services.CrudUser;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Hyperlink signupLink;

    private final CrudUser crudUser = new CrudUser();

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            errorLabel.setVisible(true);
            return;
        }

        List<User> users = crudUser.getAll();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                if (BCrypt.checkpw(password, user.getPassword_hash())) {
                    try {
                        // Vérification du rôle
                        String role = user.getRole();
                        String fxmlPath;
                        String windowTitle;

                        if ("UTILISATEUR_NORMAL".equalsIgnoreCase(role)) {
                            fxmlPath = "/front.fxml";  // ⚠️ Crée cette vue si elle n'existe pas
                            windowTitle = "Bienvenue Utilisateur";
                        } else {
                            fxmlPath = "/ListeUtilisateurs.fxml";
                            windowTitle = "Dashboard Admin";
                        }

                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                        Parent root = loader.load();

                        Stage stage = (Stage) emailField.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle(windowTitle);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        errorLabel.setText("Erreur de chargement de la page.");
                        errorLabel.setVisible(true);
                    }
                    return;
                }
            }
        }

        errorLabel.setText("Email ou mot de passe incorrect.");
        errorLabel.setVisible(true);
    }


    @FXML
    private void switchToSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Créer un compte");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forgotpassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mot de passe oublié");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
