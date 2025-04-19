package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.User;
import services.CrudUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.util.ResourceBundle;

public class AjouterUtilisateurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordVisibleField;

    @FXML private TextField roleField;
    @FXML private TextField telephoneField;
    @FXML private TextField vehiculeField;
    @FXML private CheckBox verifiedCheckBox;
    @FXML private Button enregistrerButton;


    private final CrudUser crudUser = new CrudUser();
    private User userToUpdate = null;

    // Appelée depuis ListeUtilisateursController pour passer l'utilisateur à modifier
    public void setUserToUpdate(User user) {
        this.userToUpdate = user;
        remplirChamps(user);
        enregistrerButton.setText("Modifier");
    }

    private void remplirChamps(User user) {
        nomField.setText(user.getFirst_name());
        prenomField.setText(user.getLast_name());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getTelephone());
        roleField.setText(user.getRole());
        vehiculeField.setText(user.getVehicule());
        verifiedCheckBox.setSelected(user.isVerified());
        passwordField.setText(""); // On ne remplit pas le mot de passe
        confirmPasswordField.setText("");
    }

    @FXML
    public void enregistrerUtilisateur() {
        System.out.println("==> Action enregistrerUtilisateur déclenchée");

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleField.getText();
        String telephone = telephoneField.getText();
        String vehicule = vehiculeField.getText();
        boolean verified = verifiedCheckBox.isSelected();

        // Vérification des champs requis
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Nom, prénom et email sont obligatoires.");
            return;
        }

        // En mode ajout, mot de passe requis
        if (userToUpdate == null && (password.isEmpty() || confirmPassword.isEmpty())) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe manquant", "Veuillez saisir et confirmer le mot de passe.");
            return;
        }

        // Si mot de passe saisi, il doit être confirmé
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Erreur mot de passe", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Si modification
        if (userToUpdate != null) {
            System.out.println("Modification de l'utilisateur");

            userToUpdate.setFirst_name(nom);
            userToUpdate.setLast_name(prenom);
            userToUpdate.setEmail(email);
            userToUpdate.setTelephone(telephone);
            userToUpdate.setRole(role);
            userToUpdate.setVehicule(vehicule);
            userToUpdate.setVerified(verified);

            // Mise à jour du mot de passe uniquement si rempli
            if (!password.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                userToUpdate.setPassword_hash(hashedPassword);
            }

            crudUser.update(userToUpdate);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur modifié avec succès !");
        } else {
            System.out.println("Ajout d'un nouvel utilisateur");

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User(email, hashedPassword, role, verified, nom, prenom, telephone, vehicule);
            crudUser.add(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");
        }

        // Redirection vers la liste
        goToListeUtilisateursInternal();
    }

    @FXML
    private void goToListeUtilisateurs(ActionEvent event) {
        goToListeUtilisateursInternal();
    }

    private void goToListeUtilisateursInternal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeUtilisateurs.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir la liste : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void togglePasswordVisibility() {
        if (passwordVisibleField.isVisible()) {
            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        } else {
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        }
    }

    @FXML
    private void toggleConfirmPasswordVisibility() {
        if (confirmPasswordVisibleField.isVisible()) {
            confirmPasswordField.setText(confirmPasswordVisibleField.getText());
            confirmPasswordVisibleField.setVisible(false);
            confirmPasswordVisibleField.setManaged(false);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
        } else {
            confirmPasswordVisibleField.setText(confirmPasswordField.getText());
            confirmPasswordVisibleField.setVisible(true);
            confirmPasswordVisibleField.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        }
    }

}
