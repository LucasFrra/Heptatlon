package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GestionClient extends Remote {
    Client ajouterClient(Client client) throws RemoteException;
    Client consulterClient(int clientId) throws RemoteException;
    List<Client> listerClients() throws RemoteException;
}
