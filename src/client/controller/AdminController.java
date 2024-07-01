package client.controller;

import common.GestionFacturation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.GestionClient;
import common.Client;

import java.time.LocalDate;
import java.util.List;

public class AdminController {

    @FXML
    private ListView<String> clientListView;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label revenueLabel;

    private GestionClient gestionClient;

    private GestionFacturation gestionFacturation;

    public AdminController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionClient = (GestionClient) registry.lookup("GestionClient");
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
    public void goToAddArticle(ActionEvent event) {
        loadScene(event, "/client/views/add_article.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadClients();
        clientListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Ajouter un écouteur d'événements pour double-clic
        clientListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleViewClientDetails(event);
            }
        });
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
