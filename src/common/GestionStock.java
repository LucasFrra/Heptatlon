package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GestionStock extends Remote {
    Article consulterStock(String reference, int magasinId) throws RemoteException;
    List<String> rechercherArticle(String famille, int magasinId) throws RemoteException;
    void modifierQuantiteStock(String reference, int quantite, int magasinId) throws RemoteException;
    void ajouterArticle(String nom, String reference, String famille, double prixUnitaire, String imageUrl) throws RemoteException;
    List<Article> consulterArticles(int magasinId) throws RemoteException;
    List<Article> consulterArticles() throws RemoteException;
    List<String> consulterFamilles() throws RemoteException;
    void ajouterMiseAJourPrix(Article article) throws RemoteException;
    void appliquerMisesAJourPrix() throws RemoteException;

}
