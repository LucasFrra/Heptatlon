package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import java.util.List;

public class AdminController {

    @FXML
    private ListView<String> clientListView;

    private GestionClient gestionClient;

    public AdminController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionClient = (GestionClient) registry.lookup("GestionClient");
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
                Scene scene = new Scene(loader.load(), 800, 600);

                ClientDetailsController controller = loader.getController();
                controller.setClient(selectedClient);

                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
