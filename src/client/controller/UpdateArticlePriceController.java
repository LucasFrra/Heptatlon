package client.controller;

import common.Article;
import common.GestionStock;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class UpdateArticlePriceController {
    @FXML
    private FlowPane flowPane;

    private GestionStock gestionStock;
    private List<Article> articlesModifies;

    public UpdateArticlePriceController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1010);
            gestionStock = (GestionStock) registry.lookup("GestionStock");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la connexion au serveur.");
            alert.showAndWait();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        articlesModifies = new ArrayList<>();
    }

    @FXML
    private void goToAdmin(ActionEvent event) {
        MainController mainController = new MainController();
        mainController.goToAdmin(event);
    }

    public void initialize() {
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(10));
        loadArticles();
    }

    private void loadArticles() {
        try {
            List<Article> articles = gestionStock.consulterArticles();
            updateFlowPaneWithArticles(articles);
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des articles : " + e.getMessage());
        }
    }

    private void updateFlowPaneWithArticles(List<Article> articles) {
        flowPane.getChildren().clear();
        for (Article article : articles) {
            flowPane.getChildren().add(createArticleCard(article));
        }
        if (articles.isEmpty()) {
            Text noArticlesText = new Text("Aucun article trouvé.");
            noArticlesText.setFill(javafx.scene.paint.Color.WHITE);
            flowPane.getChildren().add(noArticlesText);
        }
    }

    private VBox createArticleCard(Article article) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: white;");
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

        TextField newPriceField = new TextField();
        newPriceField.setPromptText("Nouveau prix");

        Button updateButton = new Button("Mettre à jour");
        updateButton.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: #000; -fx-font-size: 5px; -fx-font-weight: bold; -fx-border-color: #000; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-padding: 5px 20px; -fx-text-transform: uppercase;");
        updateButton.setOnAction(e -> {
            try {
                double nouveauPrix = Double.parseDouble(newPriceField.getText());
                if (nouveauPrix != article.getPrixUnitaire()) {
                    Article updatedArticle = new Article(article.getNom(), article.getReference(), article.getFamille(), nouveauPrix, article.getStock(), article.getImageUrl());
                    articlesModifies.add(updatedArticle);
                    gestionStock.ajouterMiseAJourPrix(updatedArticle);
                    showAlert("Succès", "Mise en attente", "La mise à jour du prix a été ajoutée à la liste des mises à jour en attente.");
                    loadArticles();
                }
            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Prix invalide", "Veuillez entrer un prix valide.");
            } catch (RemoteException ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur de mise à jour", "Une erreur s'est produite lors de la mise à jour du prix.");
            }
        });

        card.getChildren().addAll(imageView, refLabel, nameLabel, priceLabel, newPriceField, updateButton);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
