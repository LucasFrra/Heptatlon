package client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;
import common.ArticleFacture;
import common.Facture;
import common.GestionFacturation;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class FactureDetailsController {

    @FXML
    private Label factureIdLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label modePaiementLabel;
    @FXML
    private Label dateFacturationLabel;
    @FXML
    private TableView<ArticleFacture> articlesTableView;
    @FXML
    private TableColumn<ArticleFacture, String> referenceColumn;
    @FXML
    private TableColumn<ArticleFacture, String> articleColumn;
    @FXML
    private TableColumn<ArticleFacture, Integer> quantiteColumn;
    @FXML
    private TableColumn<ArticleFacture, Double> prixColumn;

    private GestionFacturation gestionFacturation;
    private int factureId;
    private String clientName;

    public FactureDetailsController() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            gestionFacturation = (GestionFacturation) registry.lookup("GestionFacturation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFacture(int factureId) {
        this.factureId = factureId;
        loadFactureDetails();
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    private void loadFactureDetails() {
        try {
            Facture facture = gestionFacturation.consulterFacture(factureId);
            if (facture != null) {
                ObservableList<ArticleFacture> articles = FXCollections.observableArrayList(facture.getArticles());
                articlesTableView.setItems(articles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        prixColumn.setCellFactory(tc -> new TableCell<ArticleFacture, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                }
            }
        });

        articleColumn.setCellFactory(tc -> {
            TableCell<ArticleFacture, String> cell = new TableCell<ArticleFacture, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.length() > 20 ? item.substring(0, 20) : item);
                    }
                }
            };
            return cell;
        });
    }

    @FXML
    private void goToClientDetails(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/views/client_details.fxml"));
            Scene scene = new Scene(loader.load());

            ClientDetailsController controller = loader.getController();
            controller.setClient(clientName);

            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
