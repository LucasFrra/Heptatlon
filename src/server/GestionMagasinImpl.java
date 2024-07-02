package server;

import common.GestionMagasin;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GestionMagasinImpl extends UnicastRemoteObject implements GestionMagasin {
    protected GestionMagasinImpl() throws RemoteException {
        super();
    }

    @Override
    public int validerMagasin(String nomUtilisateur, String motDePasse) throws RemoteException {
        int magasinId = -1;
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT id, mot_de_passe FROM magasins WHERE nom_utilisateur = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nomUtilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashMotDePasse = rs.getString("mot_de_passe").trim().replaceAll("\\s", ""); // Supprimer les espaces et sauts de ligne
                if (verifyPassword(motDePasse, hashMotDePasse)) {
                    magasinId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return magasinId;
    }

    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            plainPassword = plainPassword.trim(); // Ajout du trim
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainPassword.getBytes());
            String encodedHash = Base64.getEncoder().encodeToString(hash);
            return hashedPassword.equals(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}
