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
    public Article consulterStock(String reference) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM articles WHERE reference = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, reference);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Article(rs.getString("reference"), rs.getString("famille"), rs.getDouble("prix_unitaire"), rs.getInt("stock"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> rechercherArticle(String famille) throws RemoteException {
        List<String> references = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT reference FROM articles WHERE famille = ? AND stock > 0";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, famille);
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
    public void ajouterProduit(String reference, int quantite) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "UPDATE articles SET stock = stock + ? WHERE reference = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, quantite);
            stmt.setString(2, reference);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
