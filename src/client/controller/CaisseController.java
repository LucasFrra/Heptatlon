package client.controller;

import client.TestClient;
import common.Article;
import common.Client;
import common.GestionFacturation;
import common.GestionStock;
import common.GestionClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

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
    @FXML
    private ComboBox<String> familleComboBox;

    private GestionStock gestionStock;
    private GestionFacturation gestionFacturation;
    private GestionClient gestionClient;
    private ObservableList<String> panier;
    private List<Article> panierArticles;
    private boolean transactionStarted = false;
    private int magasinId;

    public CaisseController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionStock = (GestionStock) registry.lookup("GestionStock");
            gestionFacturation = (GestionFacturation) registry.lookup("GestionFacturation");
            gestionClient = (GestionClient) registry.lookup("GestionClient");
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
        this.magasinId = TestClient.getMagasinId();
        panier = FXCollections.observableArrayList();
        panierListView.setItems(panier);
        panierArticles = new ArrayList<>();

        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(10));

        loadArticles();
        loadFamilles();
    }

    private void loadArticles() {
        try {
            List<Article> articles = gestionStock.consulterArticles(magasinId);
            updateFlowPaneWithArticles(articles);
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des articles : " + e.getMessage());
        }
    }

    private void loadFamilles() {
        try {
            List<String> familles = gestionStock.consulterFamilles();
            familles.add(0, "Tous les articles");
            familleComboBox.setItems(FXCollections.observableArrayList(familles));
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des familles : " + e.getMessage());
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

        Alert accountAlert = new Alert(Alert.AlertType.CONFIRMATION);
        accountAlert.setTitle("Validation du panier");
        accountAlert.setHeaderText("Le client a-t-il un compte chez Heptatlon ?");
        accountAlert.setContentText("Sélectionnez une option.");

        ButtonType buttonTypeYes = new ButtonType("Oui");
        ButtonType buttonTypeNo = new ButtonType("Non");
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        accountAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = accountAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            demanderEmailClient();
        } else if (result.isPresent() && result.get() == buttonTypeNo) {
            demanderNouveauClient();
        }
    }

    private void demanderEmailClient() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Validation du panier");
        dialog.setHeaderText("Veuillez entrer l'adresse email du client");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            try {
                Client client = gestionClient.consulterClient(email);
                if (client != null) {
                    demanderModePaiement(client.getId());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation du panier");
                    alert.setHeaderText("Client non trouvé");
                    alert.setContentText("Aucun client trouvé avec cet email.");
                    alert.showAndWait();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la validation de la transaction : " + e.getMessage());
            }
        });
    }

    private void demanderNouveauClient() {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Validation du panier");
        dialog.setHeaderText("Veuillez entrer les informations du nouveau client");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nom Prénom");
        TextField emailField = new TextField();
        emailField.setPromptText("Email (optionnel)");

        grid.add(new Label("Nom Prénom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String email = emailField.getText().isEmpty() ? null : emailField.getText();
                return new Client(0, nameField.getText(), email);
            }
            return null;
        });

        Optional<Client> result = dialog.showAndWait();
        result.ifPresent(clientInfo -> {
            try {
                Client newClient = gestionClient.ajouterClient(new Client(0, clientInfo.getNom(), clientInfo.getEmail()));
                demanderModePaiement(newClient.getId());
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de l'ajout du nouveau client : " + e.getMessage());
            }
        });
    }

    private void demanderModePaiement(int clientId) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CB", "CB", "Chèque", "Espèces");
        dialog.setTitle("Validation du panier");
        dialog.setHeaderText("Veuillez sélectionner le mode de paiement");
        dialog.setContentText("Mode de paiement:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(modePaiement -> {
            finaliserTransaction(clientId, modePaiement);
        });
    }

    private void finaliserTransaction(int clientId, String modePaiement) {
        try {
            int transactionId = gestionFacturation.nouvelleTransaction(clientId, magasinId);

            for (Article article : panierArticles) {
                gestionFacturation.acheterArticle(transactionId, article.getReference(), article.getStock());
            }

            // Mettre à jour le mode de paiement
            gestionFacturation.mettreAJourModePaiement(transactionId, modePaiement);

            panier.clear();
            panierArticles.clear();
            transactionStarted = false;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Validation du panier");
            alert.setHeaderText("Transaction validée");
            alert.setContentText("La transaction a été validée avec succès.");
            alert.showAndWait();
            loadArticles();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la validation de la transaction : " + e.getMessage());
        }
    }

    @FXML
    private void rechercherArticlesParFamille() {
        String famille = familleComboBox.getValue();
        if (famille != null) {
            try {
                List<Article> articles;
                if ("Tous les articles".equals(famille)) {
                    articles = gestionStock.consulterArticles(magasinId);
                } else {
                    List<String> references = gestionStock.rechercherArticle(famille, magasinId);
                    articles = new ArrayList<>();
                    for (String reference : references) {
                        articles.add(gestionStock.consulterStock(reference, magasinId));
                    }
                }
                updateFlowPaneWithArticles(articles);
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la recherche des articles : " + e.getMessage());
            }
        }
    }
}
