package client.controller;

import client.TestClient;
import common.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockController {

    @FXML
    private FlowPane flowPane;
    @FXML
    private TextField searchField;
    @FXML
    private TextField quantityField;
    @FXML
    private Button searchButton;
    @FXML
    private Button modifyButton;
    @FXML
    private ListView<String> clientListView;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label revenueLabel;

    private GestionStock gestionStock;
    private GestionClient gestionClient;
    private GestionFacturation gestionFacturation;

    private int magasinId;


    public StockController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionStock = (GestionStock) registry.lookup("GestionStock");
            gestionClient = (GestionClient) registry.lookup("GestionClient");
            gestionFacturation = (GestionFacturation) registry.lookup("GestionFacturation");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la connexion au serveur.");
            alert.showAndWait();
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }

    @FXML
    private void goToMain(ActionEvent event) {
        MainController mainController = new MainController();
        mainController.goToMain(event);
    }

    public void initialize() {
        loadClients();
        clientListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Ajouter un écouteur d'événements pour double-clic
        clientListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleViewClientDetails(event);
            }
        });
        this.magasinId = TestClient.getMagasinId();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(10));

        searchButton.setOnAction(event -> rechercherArticle());
        modifyButton.setOnAction(event -> modifierQuantiteStock());

        modifyButton.disableProperty().bind(quantityField.textProperty().isEmpty());
        quantityField.setDisable(true);
    }

    private void rechercherArticle() {
        try {
            String reference = searchField.getText();
            Article article = gestionStock.consulterStock(reference, magasinId);
            if (article != null) {
                quantityField.setDisable(false);
                updateFlowPaneWithArticle(article);
            } else {
                quantityField.setDisable(true);
                flowPane.getChildren().clear();
                Text noArticlesText = new Text("Aucun article trouvé.");
                noArticlesText.setFill(javafx.scene.paint.Color.RED);
                flowPane.getChildren().add(noArticlesText);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void modifierQuantiteStock() {
        try {
            String reference = searchField.getText();
            int quantite = Integer.parseInt(quantityField.getText());
            gestionStock.modifierQuantiteStock(reference, quantite, magasinId);
            rechercherArticle(); // Rafraîchir l'affichage de l'article
        } catch (RemoteException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de modification");
            alert.setHeaderText("Quantité invalide");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateFlowPaneWithArticle(Article article) {
        flowPane.getChildren().clear();
        flowPane.getChildren().add(createArticleCard(article));
    }

    private VBox createArticleCard(Article article) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: white; -fx-cursor: hand;");
        card.setPrefSize(150, 200);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            File file = new File(article.getImageUrl());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else {
                System.out.println("Image file does not exist: " + article.getImageUrl());
            }
        }

        Label refLabel = new Label(article.getReference());
        Label nameLabel = new Label(article.getNom());
        Label priceLabel = new Label(String.format("Prix: %.2f €", article.getPrixUnitaire()));
        Label stockLabel = new Label(String.format("Stock: %d", article.getStock()));

        card.getChildren().addAll(imageView, refLabel, nameLabel, priceLabel, stockLabel);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private void loadClients() {
        try {
            List<Client> clients = gestionClient.listerClients();
            for (Client client : clients) {
                clientListView.getItems().add(client.getNom());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleViewClientDetails(MouseEvent event) {
        String selectedClient = clientListView.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/views/client_details.fxml"));
                Stage stage = (Stage) clientListView.getScene().getWindow();
                double width = stage.getWidth();
                double height = stage.getHeight();
                Scene scene = new Scene(loader.load(), width, height);

                ClientDetailsController controller = loader.getController();
                controller.setClient(selectedClient);

                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCalculateRevenue() {
        try {
            LocalDate date = datePicker.getValue();
            double revenue = gestionFacturation.calculerChiffreAffaire(date);
            revenueLabel.setText("Chiffre d'affaire: " + revenue);
        } catch (Exception e) {
            e.printStackTrace();
            revenueLabel.setText("Erreur lors du calcul du chiffre d'affaire");
        }
    }
}
