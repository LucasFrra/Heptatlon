package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ServerSelectionController {

    @FXML
    private Button centralButton;

    @FXML
    private Button magasinButton;

    @FXML
    private void handleCentralLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client/views/central_login.fxml"));
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Connexion au Serveur Central");
            Image image = new Image(getClass().getResourceAsStream("/client/images/logo.jpg"));
            primaryStage.getIcons().add(image);
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
            ((Stage) centralButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMagasinLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client/views/magasin_login.fxml"));
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Connexion au Serveur du Magasin");
            Image image = new Image(getClass().getResourceAsStream("/client/images/logo.jpg"));
            primaryStage.getIcons().add(image);
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
            ((Stage) magasinButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
