package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import common.Facture;
import common.GestionFacturation;
import common.GestionClient;
import common.Client;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.scene.Node;

public class ClientDetailsController {

    @FXML
    private Label clientNameLabel;
    @FXML
    private ListView<String> factureListView;

    private GestionFacturation gestionFacturation;
    private GestionClient gestionClient;
    private String clientName;
    private int clientId;

    public ClientDetailsController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
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
    private void goToAdmin(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/views/admin.fxml"));
            stage.setScene(new Scene(loader.load(), stage.getWidth(), stage.getHeight()));
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClient(String clientName) {
        this.clientName = clientName;
        clientNameLabel.setText(clientName);
        loadClientDetails();
        loadFactures();
    }

    private void loadClientDetails() {
        try {
            List<Client> clients = gestionClient.listerClients();
            for (Client client : clients) {
                if (client.getNom().equals(clientName)) {
                    this.clientId = client.getId();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFactures() {
        try {
            List<Facture> factures = gestionFacturation.consulterFactures(clientId);
            factureListView.getItems().clear();
            for (Facture facture : factures) {
                factureListView.getItems().add("Facture " + facture.getId() + " : " + facture.getTotal() + " €, " + facture.getModePaiement() + ", " + facture.getDateFacturation());
            }

            // Ajouter un écouteur d'événements pour double-clic
            factureListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    handleViewFactureDetails(event);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleViewFactureDetails(MouseEvent event) {
        String selectedFacture = factureListView.getSelectionModel().getSelectedItem();
        if (selectedFacture != null) {
            try {
                int factureId = Integer.parseInt(selectedFacture.split(" ")[1]);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/views/facture_details.fxml"));
                Stage stage = (Stage) factureListView.getScene().getWindow();
                double width = stage.getWidth();
                double height = stage.getHeight();
                Scene scene = new Scene(loader.load(), width, height);

                FactureDetailsController controller = loader.getController();
                controller.setFacture(factureId);
                controller.setClientName(clientName);

                stage.setScene(scene);
                stage.setMaximized(true);
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
