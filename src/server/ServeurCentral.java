package server;

import common.GestionClient;
import common.GestionFacturation;
import common.GestionMagasin;
import common.GestionStock;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServeurCentral {
    public static void main(String[] args) {
        try {
            GestionFacturation gestionFacturation = new GestionFacturationImpl();
            GestionMagasin gestionMagasin = new GestionMagasinImpl();
            GestionStock gestionStock = new GestionStockImpl();

            Registry registry = LocateRegistry.createRegistry(1010);
            registry.rebind("GestionFacturation", gestionFacturation);
            registry.rebind("GestionMagasin", gestionMagasin);
            registry.rebind("GestionStock", gestionStock);

            System.out.println("Serveur prêt.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
