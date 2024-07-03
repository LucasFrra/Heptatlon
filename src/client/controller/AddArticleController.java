package client.controller;

import common.GestionStock;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AddArticleController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField referenceField;
    @FXML
    private TextField familleField;
    @FXML
    private TextField prixUnitaireField;
    @FXML
    private TextField imageUrlField;

    private GestionStock gestionStock;
    private File selectedFile;

    public AddArticleController() {
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
    }

    @FXML
    private void handleChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            imageUrlField.setText(selectedFile.getAbsolutePath());
            showImageUploadSuccessAlert();
        }
    }

    @FXML
    private void handleAjouterArticle(ActionEvent event) {
        try {
            String nom = nomField.getText();
            String reference = referenceField.getText();
            String famille = familleField.getText();
            double prixUnitaire = Double.parseDouble(prixUnitaireField.getText());
            String serverImageUrl = uploadImage(selectedFile);

            gestionStock.ajouterArticle(nom, reference, famille, prixUnitaire, serverImageUrl);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Article ajouté");
            alert.setHeaderText(null);
            alert.setContentText("L'article a été ajouté avec succès.");
            alert.showAndWait();
            MainController mainController = new MainController();
            mainController.goToMain(event);

        } catch (RemoteException e) {
            if (e.getMessage().contains("DuplicateEntryException")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur : La référence de l'article existe déjà.");
                alert.showAndWait();
            } else if (e.getMessage().contains("DatabaseException")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de l'ajout de l'article.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur inattendue.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ajout de l'article.");
            alert.showAndWait();
        }
    }

    private String uploadImage(File file) throws IOException {
        if (file == null) return null;

        String projectDir = System.getProperty("user.dir");
        String serverPath = projectDir + "/src/client/images/articles/";
        File serverDir = new File(serverPath);
        if (!serverDir.exists()) {
            serverDir.mkdirs();
        }

        File serverFile = new File(serverPath + file.getName());

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(serverFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }

        // Afficher une alerte de confirmation après le téléchargement de l'image
        showImageUploadSuccessAlert();

        return "src/client/images/articles/" + file.getName();
    }

    private void showImageUploadSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Téléchargement d'image réussi");
        alert.setHeaderText(null);
        alert.setContentText("L'image a été téléchargée avec succès.");
        alert.showAndWait();
    }

    @FXML
    private void goToMain(ActionEvent event) {
        MainController mainController = new MainController();
        mainController.goToMain(event);
    }

    @FXML
    public void initialize() {
        imageUrlField.setEditable(false);
    }
}
