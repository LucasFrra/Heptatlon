package server;

import common.Article;
import common.GestionStock;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GestionStockImpl extends UnicastRemoteObject implements GestionStock {
    protected GestionStockImpl() throws RemoteException {
        super();
    }

    @Override
    public Article consulterStock(String reference, int magasinId) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT a.nom, a. reference, a.famille, a.prix_unitaire, a.url_image, s.stock_quantite " +
                    "FROM articles a JOIN stock s ON a.reference = s.article_ref " +
                    "WHERE a.reference = ? AND s.magasin_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, reference);
            stmt.setInt(2, magasinId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Article(rs.getString("nom"), rs.getString("reference"), rs.getString("famille"), rs.getDouble("prix_unitaire"), rs.getInt("stock_quantite"), rs.getString("url_image"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> rechercherArticle(String famille, int magasinId) throws RemoteException {
        List<String> references = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT a.reference " +
                    "FROM articles a JOIN stock s ON a.reference = s.article_ref " +
                    "WHERE a.famille = ? AND s.magasin_id = ? AND s.stock_quantite > 0";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, famille);
            stmt.setInt(2, magasinId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                references.add(rs.getString("reference"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return references;
    }

    @Override
    public void modifierQuantiteStock(String reference, int quantite, int magasinId) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "UPDATE stock SET stock_quantite = stock_quantite + ? WHERE article_ref = ? AND magasin_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, quantite);
            stmt.setString(2, reference);
            stmt.setInt(3, magasinId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void ajouterArticle(String nom, String reference, String famille, double prixUnitaire, String imageUrl) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            // Ajouter l'article dans la table articles
            String query = "INSERT INTO articles (nom, reference, famille, prix_unitaire, url_image) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nom);
            stmt.setString(2, reference);
            stmt.setString(3, famille);
            stmt.setDouble(4, prixUnitaire);
            stmt.setString(5, imageUrl);
            stmt.executeUpdate();

            // Récupérer tous les magasins
            query = "SELECT id FROM magasins";
            stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Ajouter l'article avec un stock de 0 pour chaque magasin
            query = "INSERT INTO stock (article_ref, magasin_id, stock_quantite) VALUES (?, ?, 0)";
            stmt = connection.prepareStatement(query);

            while (rs.next()) {
                int magasinId = rs.getInt("id");
                stmt.setString(1, reference);
                stmt.setInt(2, magasinId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Article> consulterArticles(int magasinId) throws RemoteException {
        List<Article> articles = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT a.nom, a.reference, a.famille, a.prix_unitaire, a.url_image, s.stock_quantite " +
                    "FROM articles a JOIN stock s ON a.reference = s.article_ref " +
                    "WHERE s.magasin_id = ? AND s.stock_quantite > 0";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, magasinId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                articles.add(new Article(
                        rs.getString("nom"),
                        rs.getString("reference"),
                        rs.getString("famille"),
                        rs.getDouble("prix_unitaire"),
                        rs.getInt("stock_quantite"),
                        rs.getString("url_image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

}
