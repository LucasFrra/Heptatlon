package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;


public class AdminController {

    @FXML
    private void goToMain(ActionEvent event) {
        MainController mainController = new MainController();
        mainController.goToMain(event);
    }

    @FXML
    public void goToAddArticle(ActionEvent event) {
        loadScene(event, "/client/views/add_article.fxml");
    }

    @FXML
    public void goToUpdateArticlePrice(ActionEvent event) {
        loadScene(event, "/client/views/update_article_price.fxml");
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
