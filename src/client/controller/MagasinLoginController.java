package client.controller;

import client.TestClient;
import common.GestionMagasin;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MagasinLoginController {

    @FXML
    private TextField nomUtilisateurField;

    @FXML
    private PasswordField motDePasseField;

    private GestionMagasin gestionMagasin;

    public MagasinLoginController() throws InterruptedException {
        boolean connected = false;
        while (!connected) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                gestionMagasin = (GestionMagasin) registry.lookup("GestionMagasin");
                connected = true;
            } catch (Exception e) {
                System.out.println("Serveur RMI non prêt, nouvelle tentative dans 1 seconde...");
                Thread.sleep(1000); // Attendre 1 seconde avant de réessayer
            }
        }
    }

    @FXML
    private void handleLogin() {
        String nomUtilisateur = nomUtilisateurField.getText();
        String motDePasse = motDePasseField.getText();

        try {
            int magasinId = gestionMagasin.validerMagasin(nomUtilisateur, motDePasse);

            if (magasinId != -1) {
                TestClient.setMagasinId(magasinId);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Connexion réussie");
                alert.setHeaderText("Connexion réussie");
                alert.setContentText("Connexion réussie au magasin");
                alert.showAndWait();
                // Accéder à l'application principale
                Parent root = FXMLLoader.load(getClass().getResource("/client/views/main.fxml"));
                Stage primaryStage = new Stage();
                primaryStage.setTitle("Gestion de Stock et Facturation");

                primaryStage.setScene(new Scene(root));
                primaryStage.setMaximized(true);
                primaryStage.show();
                ((Stage) nomUtilisateurField.getScene().getWindow()).close();
            } else {
                System.out.println("Nom d'utilisateur ou mot de passe incorrect");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de connexion");
                alert.setHeaderText("Nom d'utilisateur ou mot de passe incorrect");
                alert.setContentText("Nom d'utilisateur ou mot de passe incorrect");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
