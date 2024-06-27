package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import common.ArticleFacture;
import common.Facture;
import common.GestionFacturation;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import java.io.IOException;
import java.util.List;

public class FactureDetailsController {

    @FXML
    private Label factureIdLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label modePaiementLabel;
    @FXML
    private Label dateFacturationLabel;
    @FXML
    private ListView<String> articlesListView;

    private GestionFacturation gestionFacturation;
    private int factureId;
    private String clientName;

    public FactureDetailsController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionFacturation = (GestionFacturation) registry.lookup("GestionFacturation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFacture(int factureId) {
        this.factureId = factureId;
        loadFactureDetails();
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    private void loadFactureDetails() {
        try {
            Facture facture = gestionFacturation.consulterFacture(factureId);
            if (facture != null) {
                factureIdLabel.setText("Facture ID: " + facture.getId());
                totalLabel.setText("Total: " + facture.getTotal() + " €");
                modePaiementLabel.setText("Mode de Paiement: " + facture.getModePaiement());
                dateFacturationLabel.setText("Date: " + facture.getDateFacturation().toString());

                articlesListView.getItems().clear();
                for (ArticleFacture article : facture.getArticles()) {
                    articlesListView.getItems().add(article.getReference() + ": " + article.getQuantite() + " x " + article.getPrixUnitaire() + " € (" + article.getFamille() + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToClientDetails(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/views/client_details.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            ClientDetailsController controller = loader.getController();
            controller.setClient(clientName);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
