package client.controller;

import common.Article;
import common.GestionFacturation;
import common.GestionStock;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaisseController {

    @FXML
    private FlowPane flowPane;
    @FXML
    private ListView<String> panierListView;
    @FXML
    private VBox panierBox;
    @FXML
    private TextField quantityField;
    @FXML
    private Button validerButton;

    private GestionStock gestionStock;
    private GestionFacturation gestionFacturation;
    private ObservableList<String> panier;
    private List<Article> panierArticles;
    private boolean transactionStarted = false;
    private int magasinId = 1;

    public CaisseController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionStock = (GestionStock) registry.lookup("GestionStock");
            gestionFacturation = (GestionFacturation) registry.lookup("GestionFacturation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        panier = FXCollections.observableArrayList();
        panierListView.setItems(panier);
        panierArticles = new ArrayList<>();

        flowPane.setHgap(10);
        flowPane.setVgap(10);

        loadArticles();
    }

    private void loadArticles() {
        try {
            List<Article> articles = gestionStock.consulterArticles(1);

            for (Article article : articles) {
                flowPane.getChildren().add(createArticleCard(article));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des articles : " + e.getMessage());
        }
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
        card.setOnMouseClicked(event -> addToPanier(article));
        return card;
    }

    private void addToPanier(Article article) {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ajout au panier");
                alert.setHeaderText("Quantité invalide");
                alert.setContentText("Veuillez entrer une quantité valide.");
                alert.showAndWait();
                return;
            }

            panier.add(article.getNom() + " - Quantité: " + quantity);

            panierArticles.add(new Article(article.getNom(), article.getReference(), article.getFamille(), article.getPrixUnitaire(), quantity, article.getImageUrl()));
            System.out.println("Article ajouté au panier : " + article.getNom() + ", Quantité : " + quantity);
            quantityField.clear();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ajout au panier");
            alert.setHeaderText("Quantité invalide");
            alert.setContentText("Veuillez entrer une quantité valide.");
            alert.showAndWait();
        }
    }

    @FXML
    private void validerPanier() {
        if (panierArticles.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation du panier");
            alert.setHeaderText("Le panier est vide");
            alert.setContentText("Veuillez ajouter des articles au panier avant de valider.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Validation du panier");
        dialog.setHeaderText("Veuillez entrer l'ID du client");
        dialog.setContentText("ID du client:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(clientIdString -> {
            try {
                int clientId = Integer.parseInt(clientIdString);
                int transactionId = gestionFacturation.nouvelleTransaction(clientId, magasinId);

                for (Article article : panierArticles) {
                    gestionFacturation.acheterArticle(transactionId, article.getReference(), article.getStock());
                }

                panier.clear();
                panierArticles.clear();
                transactionStarted = false;
            } catch (NumberFormatException e) {
                System.out.println("ID du client invalide.");
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la validation de la transaction : " + e.getMessage());
            }
        });
    }
}
