package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GestionMagasin extends Remote {
    int validerMagasin(String nomUtilisateur, String motDePasse) throws RemoteException;
}
