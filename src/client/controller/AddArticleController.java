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
    @FXML
    private Button ajouterArticleButton;

    private GestionStock gestionStock;
    private File selectedFile;

    public AddArticleController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionStock = (GestionStock) registry.lookup("GestionStock");
        } catch (Exception e) {
            e.printStackTrace();
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

        // Change the path accordingly
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

        // Return the relative path from the project directory
        return "src/client/images/articles/" + file.getName();
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
