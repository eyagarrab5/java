package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.models.OffreCovoiturage;
import tn.esprit.models.Reservation;
import tn.esprit.services.ServiceOffreCovoiturage;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AcceuilController {

    @FXML
    private FlowPane offresContainer;

    @FXML
    private Button ajouterButton;

    @FXML
    private TextField searchBar;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private Button loadMoreButton;

    private List<OffreCovoiturage> allOffres; // Store all offers
    private int displayedItems = 0; // Track total number of displayed items
    private final int initialItems = 8; // 3 rows (~8 cards)
    private final int itemsPerLoad = 6; // 2 rows (~6 cards)

    @FXML
    public void initialize() {
        // Set up listeners for search, date, and sort
        searchBar.textProperty().addListener((obs, oldValue, newValue) -> renderFilteredOffres());
        datePicker.valueProperty().addListener((obs, oldValue, newValue) -> renderFilteredOffres());
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> renderFilteredOffres());
        // Load initial offers
        loadOffres();
    }

    @FXML
    void handleListeAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListesReservationRecus.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1500, 765);
            stage.setScene(scene);
            stage.setTitle("Ajouter une demande/offre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleMesOffres(ActionEvent event) {
        loadScene("/MesOffres.fxml", event);
    }

    @FXML
    void handleAutresOffres(ActionEvent event) {
        loadScene("/Acceuil.fxml", event);
    }

    private void loadScene(String fxmlPath, ActionEvent event) {
        try {
            // Load the new FXML
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            // Get the current stage from the event source
            Stage stage = (Stage) ((javafx.scene.control.MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            // Create a new scene with the loaded root and set it to the stage
            Scene scene = new Scene(root, 1500, 765);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderFilteredOffres() {
        String searchText = searchBar.getText() != null ? searchBar.getText().trim() : null;
        LocalDate selectedDate = datePicker.getValue();
        String sortOption = sortComboBox.getValue();
        ServiceOffreCovoiturage service = new ServiceOffreCovoiturage();
        allOffres = service.getFilteredOffres(searchText, selectedDate, sortOption);
        displayedItems = 0; // Reset pagination on filter change
        renderOffresPage();
    }

    public void loadOffres() {
        ServiceOffreCovoiturage service = new ServiceOffreCovoiturage();
        allOffres = service.getAutresOffresByConducteurId(123);
        displayedItems = 0; // Reset pagination
        renderOffresPage();
    }

    @FXML
    private void loadMore() {
        displayedItems += itemsPerLoad; // Increment by 6 for next load
        renderOffresPage();
    }

    private void renderOffresPage() {
        offresContainer.getChildren().clear();
        if (allOffres == null) {
            allOffres = new ArrayList<>();
        }
        if (allOffres.isEmpty()) {
            Label noDataLabel = new Label("Aucune offre disponible.");
            noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            offresContainer.getChildren().add(noDataLabel);
            loadMoreButton.setVisible(false);
            return;
        }

        // Determine how many items to display
        int itemsToShow = displayedItems == 0 ? initialItems : displayedItems + itemsPerLoad;
        int endIndex = Math.min(itemsToShow, allOffres.size());

        // Render the offers up to endIndex
        for (int i = 0; i < endIndex; i++) {
            VBox card = createOffreCard(allOffres.get(i));
            offresContainer.getChildren().add(card);
        }

        // Show or hide the Load More button
        loadMoreButton.setVisible(endIndex < allOffres.size());
    }

    private VBox createOffreCard(OffreCovoiturage offre) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        // Fixed size for uniformity with pastel blue gradient and shadow
        card.setPrefWidth(280);
        card.setPrefHeight(200);

        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #bae6fd);" +
                        "-fx-border-color: #7dd3fc;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 25;" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 3, 4);" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: center-left;" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-translate-y 0.2s ease-in-out;"
        );
        // Simple hover animation: slight scale and lift
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #bae6fd);" +
                        "-fx-border-color: #7dd3fc;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 25;" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 4, 5);" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: center-left;" +
                        "-fx-scale-x: 1.03;" +
                        "-fx-scale-y: 1.03;" +
                        "-fx-translate-y: -5;" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-translate-y 0.2s ease-in-out;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #bae6fd);" +
                        "-fx-border-color: #7dd3fc;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 25;" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 3, 4);" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: center-left;" +
                        "-fx-scale-x: 1;" +
                        "-fx-scale-y: 1;" +
                        "-fx-translate-y: 0;" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-translate-y 0.2s ease-in-out;"
        ));

        // Conductor label
        Label nameLabel = new Label(" YAACOUB EYA");
        nameLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-font-size: 19px;" +
                        "-fx-text-fill: #1e40af;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                        "-fx-padding: 0 0 5 0;"
        );

        // Trajet label with icon
        Label trajetLabel = new Label("📍 " + offre.getDepart() + " → " + offre.getDestination());
        trajetLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #1e3a8a;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                        "-fx-padding: 2 0;"
        );

        // Date label with icon
        Label dateLabel = new Label("📅 " + offre.getDate().toString());
        dateLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #1e3a8a;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                        "-fx-padding: 2 0;"
        );

        // Price label with icon
        Label prixLabel = new Label("💰 " + offre.getPrix() + " dt");
        prixLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #1e3a8a;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                        "-fx-padding: 2 0;"
        );

        // Reserve button
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 2, 3);" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-background-color 0.2s ease-in-out;"
        );
        reserverButton.setOnMouseEntered(e -> reserverButton.setStyle(
                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 3, 4);" +
                        "-fx-scale-x: 1.05;" +
                        "-fx-scale-y: 1.05;" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-background-color 0.2s ease-in-out;"
        ));
        reserverButton.setOnMouseExited(e -> reserverButton.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 2, 3);" +
                        "-fx-scale-x: 1;" +
                        "-fx-scale-y: 1;" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-background-color 0.2s ease-in-out;"
        ));
        reserverButton.setOnAction(event -> showReservationConfirmation(offre));

        // New "Voir Voiture" button
        Button voirVoitureButton = new Button("Voir Voiture");
        voirVoitureButton.setStyle(
                "-fx-background-color: #F5C45E;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 2, 3);" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-background-color 0.2s ease-in-out;"
        );
        voirVoitureButton.setOnMouseEntered(e -> voirVoitureButton.setStyle(
                "-fx-background-color: #F5C45E;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 3, 4);" +
                        "-fx-scale-x: 1.05;" +
                        "-fx-scale-y: 1.05;" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-background-color 0.2s ease-in-out;"
        ));
        voirVoitureButton.setOnMouseExited(e -> voirVoitureButton.setStyle(
                "-fx-background-color: #4f46e5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 2, 3);" +
                        "-fx-scale-x: 1;" +
                        "-fx-scale-y: 1;" +
                        "-fx-transition: -fx-scale-x 0.2s ease-in-out, -fx-scale-y 0.2s ease-in-out, -fx-background-color 0.2s ease-in-out;"
        ));
        voirVoitureButton.setOnAction(event -> showCarImagePopup(offre));

        // Button container - add the new button
        HBox buttonBox = new HBox(10, reserverButton, voirVoitureButton);
        buttonBox.setStyle("-fx-alignment: center-left; -fx-padding: 15 0 0 0;");

        card.getChildren().addAll(nameLabel, trajetLabel, dateLabel, prixLabel, buttonBox);
        return card;
    }

    private void showCarImagePopup(OffreCovoiturage offre) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Image du Véhicule");

        // Main container
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10;");
        layout.setPrefWidth(500);

        // Title
        Label title = new Label("Véhicule pour le trajet " + offre.getDepart() + " → " + offre.getDestination());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a;");

        // Image view
        ImageView carImageView = new ImageView();
        carImageView.setFitWidth(600);
        carImageView.setFitHeight(300);
        carImageView.setPreserveRatio(true);
        carImageView.setSmooth(true);
        carImageView.setCache(true);

        // Try to load the image
        try {
            if (offre.getImg() != null && !offre.getImg().isEmpty()) {
                Image image;
                // Check if the path is a URL or local file
                if (offre.getImg().startsWith("http")) {
                    image = new Image(offre.getImg());
                } else {
                    // Assuming the path is relative to the application
                    image = new Image("file:" + offre.getImg());
                }
                carImageView.setImage(image);
            } else {
                // Default image if no image is specified
                carImageView.setImage(new Image(getClass().getResourceAsStream("/img/default-car.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            carImageView.setImage(new Image(getClass().getResourceAsStream("/img/default-car.png")));
        }

        // Close button
        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 8;");
        closeButton.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(title, carImageView, closeButton);

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(ajouterButton.getScene().getWindow());
        popupStage.showAndWait();
    }

    private void showReservationConfirmation(OffreCovoiturage offre) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Confirmation de réservation");

        Label title = new Label("💬 Confirmer votre réservation");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label message = new Label("Souhaitez-vous réserver ce trajet de " + offre.getDepart() +
                " à " + offre.getDestination() + " le " + offre.getDate().toString() + " ?");
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Button btnConfirmer = new Button("✅ Confirmer");
        btnConfirmer.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-padding: 8 16 8 16; -fx-background-radius: 8;");

        Button btnAnnuler = new Button("❌ Annuler");
        btnAnnuler.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 8 16 8 16; -fx-background-radius: 8;");

        HBox buttonBox = new HBox(10, btnConfirmer, btnAnnuler);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 10 0 0 0;");

        VBox layout = new VBox(15, title, message, buttonBox);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #fefce8; -fx-border-color: #facc15; -fx-border-radius: 10; -fx-background-radius: 10;");
        layout.setPrefWidth(400);
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.setResizable(false);
        popupStage.initOwner(ajouterButton.getScene().getWindow());
        popupStage.show();

        btnConfirmer.setOnAction(e -> {
            try {
                Reservation reservation = new Reservation();
                reservation.setPassagerId(1);
                reservation.setStatut("EN_ATTENTE");
                reservation.setOffre(offre);
                reservation.setCreatedAt(LocalDateTime.now());

                ServiceReservation serviceReservation = new ServiceReservation();
                serviceReservation.add(reservation);

                popupStage.close();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Réservation réussie");
                success.setHeaderText(null);
                success.setContentText("🎉 Réservation enregistrée avec succès !");
                success.showAndWait();

            } catch (Exception ex) {
                popupStage.close();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Une erreur s’est produite lors de la réservation.");
                error.showAndWait();
            }
        });

        btnAnnuler.setOnAction(e -> popupStage.close());
    }

    @FXML
    public void handleAjouterAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1500, 765);
            stage.setScene(scene);
            stage.setTitle("Ajouter une demande/offre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}