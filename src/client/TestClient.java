package client;

import common.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.List;

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
            // gestionStock.modifierQuantiteStock("REF001", -10, magasinId);
            // System.out.println("Quantité du produit modifiée.");

            // Test de la méthode rechercherArticle
            String famille = "Sport";
            List<String> articles = gestionStock.rechercherArticle(famille, magasinId);
            if(!articles.isEmpty()) {
                System.out.println("Articles trouvés pour la famille " + famille +  ":");
                for (String ref : articles) {
                    System.out.println(" - " + ref);
                }
            } else {
                System.out.println("Aucun article trouvé pour la famille " + famille);
            }


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



            // Recherche du service GestionFacturation
            GestionFacturation gestionFacturation = (GestionFacturation) registry.lookup("GestionFacturation");

            int transactionId = gestionFacturation.nouvelleTransaction(client.getId(), magasinId);
            System.out.println("Nouvelle transaction créée pour le client : " + client.getId() + ", ID : " + transactionId);

            // Test de la méthode acheterArticle
            try {
                // Acheter plusieurs articles dans la même transaction
                gestionFacturation.acheterArticle(transactionId, "REF001", 5);
                System.out.println("Article acheté : REF001, Quantité : 5, par le client : " + client.getId());

                gestionFacturation.acheterArticle(transactionId, "REF002", 3);
                System.out.println("Article acheté : REF002, Quantité : 3, par le client : " + client.getId());

                gestionFacturation.acheterArticle(transactionId, "REF003", 2);
                System.out.println("Article acheté : REF003, Quantité : 2, par le client : " + client.getId());

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            Facture facture = gestionFacturation.consulterFacture(client.getId() - 3);
            if (facture != null) {
                System.out.println("Facture ID : " + facture.getId());
                System.out.println("Client ID : " + facture.getClientId());
                System.out.println("Total : " + facture.getTotal());
                System.out.println("Mode de paiement : " + facture.getModePaiement());
                System.out.println("Date de facturation : " + facture.getDateFacturation());
                System.out.println("Articles :");
                List<ArticleFacture> lesarticles = facture.getArticles();
                for (ArticleFacture articleFacture : lesarticles) {
                    System.out.println("  Référence : " + articleFacture.getReference());
                    System.out.println("  Famille : " + articleFacture.getFamille());
                    System.out.println("  Prix unitaire : " + articleFacture.getPrixUnitaire());
                    System.out.println("  Quantité : " + articleFacture.getQuantite());
                }
            } else {
                System.out.println("Aucune facture trouvée pour le client ID : " + (client.getId() - 1));
            }

            LocalDate date = LocalDate.now(); // Par exemple, la date d'aujourd'hui
            double chiffreAffaire = gestionFacturation.calculerChiffreAffaire(date);
            System.out.println("Chiffre d'affaire pour la date " + date + " : " + chiffreAffaire);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
