package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;

public interface GestionFacturation extends Remote {
    void acheterArticle(int clientId, String reference, int quantite) throws RemoteException;
    Facture consulterFacture(int clientId) throws RemoteException;
    void payerFacture(int clientId) throws RemoteException;
    double calculerChiffreAffaire(LocalDate date) throws RemoteException;
}
