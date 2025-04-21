package controllers;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javafx.scene.chart.CategoryAxis;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.control.DatePicker;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.models.OffreCovoiturage;
import tn.esprit.models.Reservation;
import tn.esprit.services.ServiceOffreCovoiturage;
import tn.esprit.services.ServiceReservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AllOffreController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private VBox centerContent;

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnProducts;
    @FXML
    private Button btnStats;

    // Add this method to your controller class
    @FXML
    public void showStatistics() {
        btnUsers.setStyle(getTabButtonStyle(false));
        btnProducts.setStyle(getTabButtonStyle(false));
        btnStats.setStyle(getTabButtonStyle(true));

        // Create charts
        VBox chartsContainer = new VBox(20);
        chartsContainer.setAlignment(Pos.CENTER);
        chartsContainer.setPadding(new Insets(20));

        // Chart for offers
        LineChart<String, Number> offersChart = createChart(
                "Nombre d'offres créées (5 derniers jours)",
                "Jour",
                "Nombre d'offres",
                getOfferData()
        );

        // Chart for reservations
        LineChart<String, Number> reservationsChart = createChart(
                "Nombre de réservations créées (5 derniers jours)",
                "Jour",
                "Nombre de réservations",
                getReservationData()
        );

        chartsContainer.getChildren().addAll(offersChart, reservationsChart);
        centerContent.getChildren().setAll(chartsContainer);
    }

    private LineChart<String, Number> createChart(String title, String xLabel, String yLabel, Map<Integer, Integer> data) {
        // Créer les axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);

        // Créer le chart
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setPrefSize(800, 400);
        chart.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 10px;");
        chart.setCreateSymbols(true);
        chart.setLegendVisible(false);
        chart.setAnimated(true);

        // Créer la série
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Évolution journalière");

        String[] dayLabels = {"J-4", "J-3", "J-2", "J-1", "Aujourd'hui"};
        for (int i = 0; i < 5; i++) {
            series.getData().add(new XYChart.Data<>(dayLabels[i], data.get(i)));
        }

        chart.getData().add(series);

        // Appliquer le style aux points après leur rendu
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> d : series.getData()) {
                Node node = d.getNode();
                node.setStyle(
                        "-fx-background-color: #3498db, white;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-background-insets: 0, 2;" +
                                "-fx-shape: 'M0,4 L4,0 L8,4 L4,8 Z';"
                );

                // Effet de survol
                Tooltip tooltip = new Tooltip("Jour: " + d.getXValue() + "\nValeur: " + d.getYValue());
                tooltip.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #333333; -fx-border-color: #3498db;");
                Tooltip.install(node, tooltip);

                node.setOnMouseEntered(e -> node.setStyle("-fx-background-color: #2ecc71, white; -fx-background-radius: 6px;"));
                node.setOnMouseExited(e -> node.setStyle(
                        "-fx-background-color: #3498db, white;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-background-insets: 0, 2;" +
                                "-fx-shape: 'M0,4 L4,0 L8,4 L4,8 Z';"
                ));
            }
        });

        return chart;
    }
    private Map<Integer, Integer> getOfferData() {
        Map<Integer, Integer> dayCounts = new HashMap<>();
        LocalDate today = LocalDate.now();

        // Initialize last 5 days with count 0
        for (int i = 4; i >= 0; i--) {
            dayCounts.put(i, 0);
        }

        // Count offers for each day
        for (OffreCovoiturage offre : offreService.getAll()) {
            LocalDate offerDate = offre.getDate().toLocalDate();
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(offerDate, today);

            if (daysBetween >= 0 && daysBetween <= 4) {
                int dayKey = (int) (4 - daysBetween); // Reverse order (0 = 5 days ago, 4 = today)
                dayCounts.put(dayKey, dayCounts.get(dayKey) + 1);
            }
        }

        return dayCounts;
    }

    private Map<Integer, Integer> getReservationData() {
        Map<Integer, Integer> dayCounts = new HashMap<>();
        LocalDate today = LocalDate.now();

        // Initialize last 5 days with count 0
        for (int i = 4; i >= 0; i--) {
            dayCounts.put(i, 0);
        }

        // Count reservations for each day
        for (Reservation reservation : reservationService.getAll()) {
            LocalDate reservationDate = reservation.getCreatedAt().toLocalDate();
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(reservationDate, today);

            if (daysBetween >= 0 && daysBetween <= 4) {
                int dayKey = (int) (4 - daysBetween); // Reverse order (0 = 5 days ago, 4 = today)
                dayCounts.put(dayKey, dayCounts.get(dayKey) + 1);
            }
        }

        return dayCounts;
    }
    private TableView<OffreCovoiturage> offreTable;
    private TableView<Reservation> reservationTable;

    private final ServiceOffreCovoiturage offreService = new ServiceOffreCovoiturage();
    private final ServiceReservation reservationService = new ServiceReservation();

    @FXML
    public void initialize() {
        Label welcomeLabel = new Label("Hello Admouna");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        centerContent.getChildren().setAll(welcomeLabel);

        // Style des boutons pour les onglets
        btnUsers.setStyle(getTabButtonStyle(false));
        btnProducts.setStyle(getTabButtonStyle(false));
    }

    private String getTabButtonStyle(boolean isSelected) {
        return "-fx-background-color: " + (isSelected ? "#3498db" : "#f8f9fa") + "; " +
                "-fx-text-fill: " + (isSelected ? "white" : "#2c3e50") + "; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: #dee2e6; " +
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); " +
                "-fx-cursor: hand;";
    }

    @FXML
    public void showOffres() {
        btnUsers.setStyle(getTabButtonStyle(true));
        btnProducts.setStyle(getTabButtonStyle(false));

        if (offreTable == null) {
            offreTable = createOffreTable();
            loadOffres();
        }
        centerContent.getChildren().setAll(offreTable);
    }

    @FXML
    public void showReservations() {
        btnUsers.setStyle(getTabButtonStyle(false));
        btnProducts.setStyle(getTabButtonStyle(true));

        if (reservationTable == null) {
            reservationTable = createReservationTable();
            loadReservations();
        }
        centerContent.getChildren().setAll(reservationTable);
    }

    private TableView<OffreCovoiturage> createOffreTable() {
        TableView<OffreCovoiturage> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: #e0e6f0; " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 10; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-text-fill: #1a2b4c; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2); " +
                        "-fx-selection-bar: #e6f3ff; " +
                        "-fx-selection-bar-non-focused: #f0f6ff;"
        );
        table.setMaxWidth(900);
        table.setMaxHeight(500);

        // Style commun pour les colonnes
        String columnStyle = "-fx-alignment: CENTER; " +
                "-fx-background-color: #f8fafc; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #1a2b4c; " +
                "-fx-padding: 8; " +
                "-fx-border-color: #e0e6f0; " +
                "-fx-border-width: 0 1 1 0;";

        // Style pour les cellules
        String cellStyle = "-fx-padding: 8; " +
                "-fx-border-color: #e0e6f0; " +
                "-fx-border-width: 0 1 1 0; " +
                "-fx-alignment: CENTER;";

        TableColumn<OffreCovoiturage, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setPrefWidth(40);
        idColumn.setStyle(columnStyle);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle(cellStyle);
            }
        });

        TableColumn<OffreCovoiturage, String> departColumn = new TableColumn<>("Départ");
        departColumn.setPrefWidth(100);
        departColumn.setStyle(columnStyle);
        departColumn.setCellValueFactory(new PropertyValueFactory<>("depart"));
        departColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(cellStyle + "-fx-alignment: CENTER-LEFT;");
            }
        });

        TableColumn<OffreCovoiturage, Integer> conducteurIdColumn = new TableColumn<>("Cond. ID");
        conducteurIdColumn.setPrefWidth(80);
        conducteurIdColumn.setStyle(columnStyle);
        conducteurIdColumn.setCellValueFactory(new PropertyValueFactory<>("conducteurId"));
        conducteurIdColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle(cellStyle);
            }
        });

        TableColumn<OffreCovoiturage, String> destinationColumn = new TableColumn<>("Destination");
        destinationColumn.setPrefWidth(100);
        destinationColumn.setStyle(columnStyle);
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        destinationColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(cellStyle + "-fx-alignment: CENTER-LEFT;");
            }
        });

        TableColumn<OffreCovoiturage, Integer> matVehiculeColumn = new TableColumn<>("Mat. Véh.");
        matVehiculeColumn.setPrefWidth(80);
        matVehiculeColumn.setStyle(columnStyle);
        matVehiculeColumn.setCellValueFactory(new PropertyValueFactory<>("matVehicule"));
        matVehiculeColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle(cellStyle);
            }
        });

        TableColumn<OffreCovoiturage, Integer> placesDispoColumn = new TableColumn<>("Places");
        placesDispoColumn.setPrefWidth(60);
        placesDispoColumn.setStyle(columnStyle);
        placesDispoColumn.setCellValueFactory(new PropertyValueFactory<>("placesDispo"));
        placesDispoColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle(cellStyle);
            }
        });

        TableColumn<OffreCovoiturage, LocalDateTime> dateColumn = new TableColumn<>("Date");
        dateColumn.setPrefWidth(120);
        dateColumn.setStyle(columnStyle);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
                setStyle(cellStyle + "-fx-alignment: CENTER-LEFT;");
            }
        });

        TableColumn<OffreCovoiturage, String> statutColumn = new TableColumn<>("Statut");
        statutColumn.setPrefWidth(80);
        statutColumn.setStyle(columnStyle);
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statutColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(cellStyle);
            }
        });

        TableColumn<OffreCovoiturage, Float> prixColumn = new TableColumn<>("Prix");
        prixColumn.setPrefWidth(60);
        prixColumn.setStyle(columnStyle);
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prixColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("%.2f", item));
                setStyle(cellStyle);
            }
        });

        TableColumn<OffreCovoiturage, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setPrefWidth(150);
        actionColumn.setStyle(columnStyle);
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Éditer");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttonsContainer = new HBox(5, editButton, deleteButton);

            {
                buttonsContainer.setStyle("-fx-alignment: CENTER;");
                editButton.setStyle(
                        "-fx-background-color: #3498db; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 5 10; " +
                                "-fx-background-radius: 5;"
                );
                editButton.setOnAction(event -> {
                    OffreCovoiturage offre = getTableView().getItems().get(getIndex());
                    editOffre(offre);
                });

                deleteButton.setStyle(
                        "-fx-background-color: #e74c3c; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 5 10; " +
                                "-fx-background-radius: 5;"
                );
                deleteButton.setOnAction(event -> {
                    OffreCovoiturage offre = getTableView().getItems().get(getIndex());
                    offreService.delete(offre);
                    loadOffres();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsContainer);
                }
                setStyle(cellStyle);
            }
        });

        table.getColumns().addAll(
                idColumn, departColumn, conducteurIdColumn, destinationColumn,
                matVehiculeColumn, placesDispoColumn, dateColumn, statutColumn, prixColumn, actionColumn
        );

        // Style pour les lignes
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(OffreCovoiturage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-border-color: #e0e6f0; -fx-border-width: 0 0 1 0;");
                }
            }
        });

        return table;
    }

    private void editOffre(OffreCovoiturage offre) {
        Dialog<OffreCovoiturage> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'offre");
        dialog.setHeaderText("Modifier les détails de l'offre");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField departField = new TextField(offre.getDepart());
        TextField destinationField = new TextField(offre.getDestination());
        TextField placesField = new TextField(String.valueOf(offre.getPlacesDispo()));
        TextField prixField = new TextField(String.valueOf(offre.getPrix()));
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(offre.getDate().toLocalDate());

        grid.add(new Label("Départ:"), 0, 0);
        grid.add(departField, 1, 0);
        grid.add(new Label("Destination:"), 0, 1);
        grid.add(destinationField, 1, 1);
        grid.add(new Label("Places disponibles:"), 0, 2);
        grid.add(placesField, 1, 2);
        grid.add(new Label("Prix:"), 0, 3);
        grid.add(prixField, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                offre.setDepart(departField.getText());
                offre.setDestination(destinationField.getText());
                offre.setPlacesDispo(Integer.parseInt(placesField.getText()));
                offre.setPrix(Float.parseFloat(prixField.getText()));
                offre.setDate(datePicker.getValue().atStartOfDay());
                return offre;
            }
            return null;
        });

        Optional<OffreCovoiturage> result = dialog.showAndWait();

        result.ifPresent(updatedOffre -> {
            offreService.update(updatedOffre);
            loadOffres();
        });
    }

    private TableView<Reservation> createReservationTable() {
        TableView<Reservation> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: #e0e6f0; " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 10; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-text-fill: #1a2b4c; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2); " +
                        "-fx-selection-bar: #e6f3ff; " +
                        "-fx-selection-bar-non-focused: #f0f6ff;"
        );
        table.setMaxWidth(900);
        table.setFixedCellSize(40); // Set a fixed height for each row (adjust as needed)

        // Style commun pour les colonnes
        String columnStyle = "-fx-alignment: CENTER; " +
                "-fx-background-color: #f8fafc; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #1a2b4c; " +
                "-fx-padding: 8; " +
                "-fx-border-color: #e0e6f0; " +
                "-fx-border-width: 0 1 1 0;";

        // Style pour les cellules
        String cellStyle = "-fx-padding: 8; " +
                "-fx-border-color: #e0e6f0; " +
                "-fx-border-width: 0 1 1 0; " +
                "-fx-alignment: CENTER;";

        TableColumn<Reservation, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setPrefWidth(40);
        idColumn.setStyle(columnStyle);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle(cellStyle);
            }
        });

        TableColumn<Reservation, Integer> offreIdColumn = new TableColumn<>("Offre ID");
        offreIdColumn.setPrefWidth(60);
        offreIdColumn.setStyle(columnStyle);
        offreIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getOffre().getId()).asObject());
        offreIdColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle(cellStyle);
            }
        });

        TableColumn<Reservation, String> departColumn = new TableColumn<>("Départ");
        departColumn.setPrefWidth(100);
        departColumn.setStyle(columnStyle);
        departColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOffre().getDepart()));
        departColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(cellStyle + "-fx-alignment: CENTER-LEFT;");
            }
        });

        TableColumn<Reservation, String> destinationColumn = new TableColumn<>("Destination");
        destinationColumn.setPrefWidth(100);
        destinationColumn.setStyle(columnStyle);
        destinationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOffre().getDestination()));
        destinationColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(cellStyle + "-fx-alignment: CENTER-LEFT;");
            }
        });

        TableColumn<Reservation, LocalDateTime> offreDateColumn = new TableColumn<>("Date Offre");
        offreDateColumn.setPrefWidth(120);
        offreDateColumn.setStyle(columnStyle);
        offreDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getOffre().getDate()));
        offreDateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
                setStyle(cellStyle + "-fx-alignment: CENTER-LEFT;");
            }
        });

        TableColumn<Reservation, Integer> passagerIdColumn = new TableColumn<>("Passager ID");
        passagerIdColumn.setPrefWidth(80);
        passagerIdColumn.setStyle(columnStyle);
        passagerIdColumn.setCellValueFactory(new PropertyValueFactory<>("passagerId"));
        passagerIdColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle(cellStyle);
            }
        });

        TableColumn<Reservation, LocalDateTime> createdAtColumn = new TableColumn<>("Date Rés.");
        createdAtColumn.setPrefWidth(120);
        createdAtColumn.setStyle(columnStyle);
        createdAtColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCreatedAt()));
        createdAtColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
                setStyle(cellStyle + "-fx-alignment: CENTER-LEFT;");
            }
        });

        TableColumn<Reservation, String> statutColumn = new TableColumn<>("Statut");
        statutColumn.setPrefWidth(80);
        statutColumn.setStyle(columnStyle);
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statutColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(cellStyle);
            }
        });

        TableColumn<Reservation, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setPrefWidth(150);
        actionColumn.setStyle(columnStyle);
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Éditer");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttonsContainer = new HBox(5, editButton, deleteButton);

            {
                buttonsContainer.setStyle("-fx-alignment: CENTER;");
                editButton.setStyle(
                        "-fx-background-color: #3498db; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 5 10; " +
                                "-fx-background-radius: 5;"
                );
                editButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    editReservation(reservation);
                });

                deleteButton.setStyle(
                        "-fx-background-color: #e74c3c; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 5 10; " +
                                "-fx-background-radius: 5;"
                );
                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    reservationService.delete(reservation);
                    loadReservations();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsContainer);
                }
                setStyle(cellStyle);
            }
        });

        table.getColumns().addAll(
                idColumn, offreIdColumn, departColumn, destinationColumn, offreDateColumn,
                passagerIdColumn, createdAtColumn, statutColumn, actionColumn
        );

        // Style pour les lignes et empêcher l'affichage des lignes vides
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    setPrefHeight(0); // Ensure empty rows have zero height
                    setVisible(false); // Hide empty rows
                } else {
                    setStyle("-fx-border-color: #e0e6f0; -fx-border-width: 0 0 1 0;");
                    setPrefHeight(table.getFixedCellSize()); // Use fixed cell size
                    setVisible(true); // Show populated rows
                }
            }
        });

        // Dynamically adjust table height based on number of items
        table.itemsProperty().addListener((obs, oldItems, newItems) -> {
            double rowHeight = table.getFixedCellSize();
            double headerHeight = 40; // Approximate height of the table header (adjust if needed)
            double totalHeight = newItems.size() * rowHeight + headerHeight;
            table.setPrefHeight(Math.min(totalHeight, 500)); // Cap at max height if necessary
        });

        return table;
    }

    private void editReservation(Reservation reservation) {
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Modifier la réservation");
        dialog.setHeaderText("Modifier les détails de la réservation");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField passagerIdField = new TextField(String.valueOf(reservation.getPassagerId()));
        TextField offreIdField = new TextField(String.valueOf(reservation.getOffre().getId()));
        TextField statutField = new TextField(reservation.getStatut());
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(reservation.getCreatedAt().toLocalDate());

        grid.add(new Label("Statut:"), 0, 2);
        grid.add(statutField, 1, 2);
        grid.add(new Label("Date Réservation:"), 0, 3);
        grid.add(datePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                reservation.setPassagerId(Integer.parseInt(passagerIdField.getText()));
                OffreCovoiturage offre = offreService.getById(Integer.parseInt(offreIdField.getText()));
                reservation.setOffre(offre);
                reservation.setStatut(statutField.getText());
                reservation.setCreatedAt(datePicker.getValue().atStartOfDay());
                return reservation;
            }
            return null;
        });

        Optional<Reservation> result = dialog.showAndWait();

        result.ifPresent(updatedReservation -> {
            reservationService.update(updatedReservation);
            loadReservations();
        });
    }

    private void loadOffres() {
        ObservableList<OffreCovoiturage> offres = FXCollections.observableArrayList(offreService.getAll());
        offreTable.setItems(offres);
    }

    private void loadReservations() {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList(reservationService.getAll());
        reservationTable.setItems(reservations);
    }
}