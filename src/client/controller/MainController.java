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
        loadScene(event, "/client/views/caisse.fxml");
    }

    @FXML
    public void goToAdmin(ActionEvent event) {
        loadScene(event, "/client/views/admin.fxml");
    }

    @FXML
    public void goToMain(ActionEvent event) {
        loadScene(event, "/client/views/main.fxml");
    }

    @FXML
    public void goToStock(ActionEvent event) {
        loadScene(event, "/client/views/stock.fxml");
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
}
