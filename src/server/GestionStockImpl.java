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
            String query = "SELECT a. reference, a.famille, a.prix_unitaire, s.stock_quantite " +
                    "FROM articles a JOIN stock s ON a.reference = s.article_ref " +
                    "WHERE a.reference = ? AND s.magasin_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, reference);
            stmt.setInt(2, magasinId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Article(rs.getString("reference"), rs.getString("famille"), rs.getDouble("prix_unitaire"), rs.getInt("stock_quantite"));
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
}
