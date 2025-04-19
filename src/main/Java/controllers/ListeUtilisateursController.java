package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.User;
import services.CrudUser;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeUtilisateursController implements Initializable {

    @FXML private TableView<User> tableViewUsers;
    @FXML private TableColumn<User, Integer> idCol;
    @FXML private TableColumn<User, String> firstNameCol;
    @FXML private TableColumn<User, String> lastNameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> password_hashcol;
    @FXML private TableColumn<User, String> telephoneCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TableColumn<User, String> vehiculeCol;
    @FXML private TableColumn<User, Boolean> verifiedCol;
    @FXML private TableColumn<User, Void> actionCol;

    private final CrudUser crudUser = new CrudUser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadUsers();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        password_hashcol.setCellValueFactory(new PropertyValueFactory<>("password_hash"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        verifiedCol.setCellValueFactory(new PropertyValueFactory<>("verified"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        vehiculeCol.setCellValueFactory(new PropertyValueFactory<>("vehicule"));

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                btnEdit.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterutilisateur.fxml"));
                        Parent root = loader.load();

                        AjouterUtilisateurController controller = loader.getController();
                        controller.setUserToUpdate(user);

                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible de charger le formulaire de modification.");
                    }
                });

                btnDelete.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Confirmation de suppression");
                    confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
                    confirmation.setContentText("Utilisateur : " + user.getFirst_name() + " " + user.getLast_name());

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            crudUser.delete(user.getId());
                            loadUsers();
                            showAlert("Succès", "Utilisateur supprimé avec succès !");
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadUsers() {
        List<User> userList = crudUser.getAll();
        tableViewUsers.getItems().setAll(userList);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void goToAjouterUtilisateur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterutilisateur.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tableViewUsers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire d'ajout.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml")); // <-- Ici, "Login.fxml"
            Parent root = loader.load();

            Stage stage = (Stage) tableViewUsers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir à la page de connexion.");
        }
    }

}
