package server;

import common.Facture;
import common.GestionFacturation;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class GestionFacturationImpl extends UnicastRemoteObject implements GestionFacturation {
    protected GestionFacturationImpl() throws RemoteException {
        super();
    }

    @Override
    public void acheterArticle(int clientId, String reference, int quantite) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            // Logique pour enregistrer l'achat dans les factures et factures_articles
            String insertFacture = "INSERT INTO factures (client_id, total, mode_paiement, date_facturation) VALUES (?, 0, 'non payé', CURDATE())";
            PreparedStatement stmtFacture = connection.prepareStatement(insertFacture, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtFacture.setInt(1, clientId);
            stmtFacture.executeUpdate();
            ResultSet rs = stmtFacture.getGeneratedKeys();
            if (rs.next()) {
                int factureId = rs.getInt(1);
                String insertFactureArticle = "INSERT INTO factures_articles (facture_id, article_reference, quantite) VALUES (?, ?, ?)";
                PreparedStatement stmtFactureArticle = connection.prepareStatement(insertFactureArticle);
                stmtFactureArticle.setInt(1, factureId);
                stmtFactureArticle.setString(2, reference);
                stmtFactureArticle.setInt(3, quantite);
                stmtFactureArticle.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Facture consulterFacture(int clientId) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM factures WHERE client_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Facture(rs.getInt("id"), rs.getInt("client_id"), rs.getDouble("total"), rs.getString("mode_paiement"), rs.getDate("date_facturation").toLocalDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void payerFacture(int clientId) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "UPDATE factures SET mode_paiement = 'payé' WHERE client_id = ? AND mode_paiement = 'non payé'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, clientId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public double calculerChiffreAffaire(LocalDate date) throws RemoteException {
        double total = 0;
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT SUM(total) AS total FROM factures WHERE date_facturation = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}
