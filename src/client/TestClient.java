package client;

import common.Article;
import common.GestionStock;
import common.GestionClient;
import common.Client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestClient {
    public static void main(String[] args) {
        try {
            // Connexion au registre RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Recherche du service GestionStock
            GestionStock gestionStock = (GestionStock) registry.lookup("GestionStock");

            int magasinId = 1; // Exemple d'ID de magasin

            // Test de la méthode consulterStock
            Article article = gestionStock.consulterStock("REF001", magasinId);
            if (article != null) {
                System.out.println("Article trouvé : " + article.getReference() + ", Quantité en stock : " + article.getStock());
            } else {
                System.out.println("Article non trouvé.");
            }

            // Test de la méthode modifierQuantiteStock
            gestionStock.modifierQuantiteStock("REF001", -10, magasinId);
            System.out.println("Quantité du produit modifiée.");

            // Recherche du service GestionClient
            GestionClient gestionClient = (GestionClient) registry.lookup("GestionClient");

            // Test de la méthode ajouterClient
            Client client = new Client(0, "Jean Dupont", "jean.dupont@example.com");
            client = gestionClient.ajouterClient(client);
            System.out.println("Client ajouté avec ID: " + client.getId());

            // Test de la méthode consulterClient
            Client clientRetrieved = gestionClient.consulterClient(client.getId());
            if (clientRetrieved != null) {
                System.out.println("Client trouvé : " + clientRetrieved.getNom() + ", Email : " + clientRetrieved.getEmail());
            } else {
                System.out.println("Client non trouvé.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
