package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GestionStock extends Remote {
    Article consulterStock(String reference) throws RemoteException;
    List<String> rechercherArticle(String famille) throws RemoteException;
    void modifierQuantiteStock(String reference, int quantite) throws RemoteException;
}
