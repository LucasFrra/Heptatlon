package client.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class MainController {

    @FXML
    private void goToCaisse(ActionEvent event) {
        loadScene(event, "/client/views/caisse.fxml", 800, 600);
    }

    @FXML
    private void goToAdmin(ActionEvent event) {
        loadScene(event, "/client/views/admin.fxml", 800, 600);
    }

    @FXML
    public void goToMain(ActionEvent event) {
        loadScene(event, "/client/views/main.fxml", 800, 600);
    }

    private void loadScene(ActionEvent event, String fxmlFile, int width, int height) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
