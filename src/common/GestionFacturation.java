package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface GestionFacturation extends Remote {
    int nouvelleTransaction(int clientId, int magasinId) throws RemoteException;
    void acheterArticle(int clientId, String reference, int quantite) throws RemoteException;
    Facture consulterFacture(int factureId) throws RemoteException;
    double calculerChiffreAffaire(LocalDate date, int magasinId) throws RemoteException;
    List<Facture> consulterFactures(int clientId) throws RemoteException;
    void mettreAJourModePaiement(int transactionId, String modePaiement) throws RemoteException; // Nouvelle méthode
}
