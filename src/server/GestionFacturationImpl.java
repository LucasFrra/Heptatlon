package server;

import common.ArticleFacture;
import common.Facture;
import common.GestionFacturation;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GestionFacturationImpl extends UnicastRemoteObject implements GestionFacturation {
    protected GestionFacturationImpl() throws RemoteException {
        super();
    }

    @Override
    public int nouvelleTransaction(int clientId, int magasinId) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "INSERT INTO factures (client_id, total, date_facturation, magasin_id) VALUES (?, 0, CURDATE(), ?)";
            PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, clientId);
            stmt.setInt(2, magasinId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Erreur lors de la création de la transaction.", e);
        }
    }

    @Override
    public void acheterArticle(int transactionId, String reference, int quantite) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            // Vérifier si l'article est en stock
            String checkStock = "SELECT stock_quantite FROM stock WHERE article_ref = ? AND magasin_id = (SELECT magasin_id FROM factures WHERE id = ?)";
            PreparedStatement stmtCheckStock = connection.prepareStatement(checkStock);
            stmtCheckStock.setString(1, reference);
            stmtCheckStock.setInt(2, transactionId);
            ResultSet rsStock = stmtCheckStock.executeQuery();

            if (rsStock.next() && rsStock.getInt("stock_quantite") >= quantite) {
                // Réduire la quantité de l'article en stock
                String updateStock = "UPDATE stock SET stock_quantite = stock_quantite - ? WHERE article_ref = ? AND magasin_id = (SELECT magasin_id FROM factures WHERE id = ?)";
                PreparedStatement stmtUpdateStock = connection.prepareStatement(updateStock);
                stmtUpdateStock.setInt(1, quantite);
                stmtUpdateStock.setString(2, reference);
                stmtUpdateStock.setInt(3, transactionId);
                stmtUpdateStock.executeUpdate();

                // Ajouter l'article à la facture
                String insertFactureArticle = "INSERT INTO factures_articles (facture_id, article_reference, quantite) VALUES (?, ?, ?)";
                PreparedStatement stmtFactureArticle = connection.prepareStatement(insertFactureArticle);
                stmtFactureArticle.setInt(1, transactionId);
                stmtFactureArticle.setString(2, reference);
                stmtFactureArticle.setInt(3, quantite);
                stmtFactureArticle.executeUpdate();

                // Récupérer le prix de l'article
                String getArticlePrice = "SELECT prix_unitaire FROM articles WHERE reference = ?";
                PreparedStatement stmtGetPrice = connection.prepareStatement(getArticlePrice);
                stmtGetPrice.setString(1, reference);
                ResultSet rsPrice = stmtGetPrice.executeQuery();
                double prixUnitaire = 0;
                if (rsPrice.next()) {
                    prixUnitaire = rsPrice.getDouble("prix_unitaire");
                }

                // Mettre à jour le total de la facture
                String updateTotal = "UPDATE factures SET total = total + ? WHERE id = ?";
                PreparedStatement stmtUpdateTotal = connection.prepareStatement(updateTotal);
                stmtUpdateTotal.setDouble(1, prixUnitaire * quantite);
                stmtUpdateTotal.setInt(2, transactionId);
                stmtUpdateTotal.executeUpdate();
            } else {
                throw new RemoteException("Quantité insuffisante en stock pour l'article : " + reference);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Erreur lors de l'achat de l'article", e);
        }
    }

    @Override
    public Facture consulterFacture(int factureId) throws RemoteException {
        try (Connection connection = DBConnection.getConnection()) {
            // Récupérer la facture
            String query = "SELECT * FROM factures WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, factureId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int clientId = rs.getInt("client_id");
                double total = rs.getDouble("total");
                String modePaiement = rs.getString("mode_paiement");
                LocalDate dateFacturation = rs.getDate("date_facturation").toLocalDate();

                // Récupérer les articles de la facture
                String queryArticles = "SELECT fa.article_reference, a.famille, a.prix_unitaire, fa.quantite " +
                        "FROM factures_articles fa " +
                        "JOIN articles a ON fa.article_reference = a.reference " +
                        "WHERE fa.facture_id = ?";
                PreparedStatement stmtArticles = connection.prepareStatement(queryArticles);
                stmtArticles.setInt(1, factureId);
                ResultSet rsArticles = stmtArticles.executeQuery();

                List<ArticleFacture> articles = new ArrayList<>();
                while (rsArticles.next()) {
                    String reference = rsArticles.getString("article_reference");
                    String famille = rsArticles.getString("famille");
                    double prixUnitaire = rsArticles.getDouble("prix_unitaire");
                    int quantite = rsArticles.getInt("quantite");
                    articles.add(new ArticleFacture(reference, famille, prixUnitaire, quantite));
                }

                return new Facture(factureId, clientId, total, modePaiement, dateFacturation, articles);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Erreur lors de la consultation de la facture", e);
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

        BigDecimal totalRounded = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
        return totalRounded.doubleValue();
    }

    @Override
    public List<Facture> consulterFactures(int clientId) throws RemoteException {
        List<Facture> factures = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT id, total, mode_paiement, date_facturation FROM factures WHERE client_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                double total = rs.getDouble("total");
                String modePaiement = rs.getString("mode_paiement");
                LocalDate dateFacturation = rs.getDate("date_facturation").toLocalDate();
                factures.add(new Facture(id, clientId, total, modePaiement, dateFacturation, new ArrayList<>()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Erreur lors de la récupération des factures du client", e);
        }
        return factures;
    }
}