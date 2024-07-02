package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TestClient extends Application {

    private static int magasinId;

    public static void setMagasinId(int id) {
        magasinId = id;
    }

    public static int getMagasinId() {
        return magasinId;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/magasin_login.fxml"));
        primaryStage.setTitle("Connexion au Magasin");

        Image image = new Image(getClass().getResourceAsStream("images/logo.jpg"));
        primaryStage.getIcons().add(image);

        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
