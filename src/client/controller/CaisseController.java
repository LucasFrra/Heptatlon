package client.controller;

import common.GestionFacturation;
import common.Facture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;

public class CaisseController {

    @FXML
    private TextField clientIdField;
    @FXML
    private TextField magasinIdField;
    @FXML
    private TextField articleRefField;
    @FXML
    private TextField quantiteField;
    @FXML
    private TextField dateField;
    @FXML
    private Label transactionLabel;
    @FXML
    private Label achatStatusLabel;
    @FXML
    private Label factureLabel;
    @FXML
    private Label revenueLabel;

    private GestionFacturation gestionFacturation;
    private int transactionId;

    public CaisseController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionFacturation = (GestionFacturation) registry.lookup("GestionFacturation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMain(ActionEvent event) {
        MainController mainController = new MainController();
        mainController.goToMain(event);
    }


    @FXML
    private void handleStartTransaction() {
        try {
            int clientId = Integer.parseInt(clientIdField.getText());
            int magasinId = Integer.parseInt(magasinIdField.getText());
            transactionId = gestionFacturation.nouvelleTransaction(clientId, magasinId);
            transactionLabel.setText("Transaction ID: " + transactionId);
        } catch (Exception e) {
            e.printStackTrace();
            transactionLabel.setText("Erreur lors de la création de la transaction");
        }
    }

    @FXML
    private void handleBuyArticle() {
        try {
            String reference = articleRefField.getText();
            int quantite = Integer.parseInt(quantiteField.getText());
            gestionFacturation.acheterArticle(transactionId, reference, quantite);
            achatStatusLabel.setText("Article acheté: " + reference);
        } catch (Exception e) {
            e.printStackTrace();
            achatStatusLabel.setText("Erreur lors de l'achat de l'article");
        }
    }

    @FXML
    private void handleConsultFacture() {
        try {
            int clientId = Integer.parseInt(clientIdField.getText());
            Facture facture = gestionFacturation.consulterFacture(clientId);
            if (facture != null) {
                factureLabel.setText("Facture ID: " + facture.getId() + ", Total: " + facture.getTotal());
            } else {
                factureLabel.setText("Aucune facture trouvée pour le client ID: " + clientId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            factureLabel.setText("Erreur lors de la consultation de la facture");
        }
    }

    @FXML
    private void handleCalculateRevenue() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            double revenue = gestionFacturation.calculerChiffreAffaire(date);
            revenueLabel.setText("Chiffre d'affaire: " + revenue);
        } catch (Exception e) {
            e.printStackTrace();
            revenueLabel.setText("Erreur lors du calcul du chiffre d'affaire");
        }
    }
}
