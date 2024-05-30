package server;

import common.GestionStock;
import common.GestionFacturation;
import common.GestionClient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Serveur {
    public static void main(String[] args) {
        try {
            GestionStock gestionStock = new GestionStockImpl();
            GestionFacturation gestionFacturation = new GestionFacturationImpl();
            GestionClient gestionClient = new GestionClientImpl();

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("GestionStock", gestionStock);
            registry.rebind("GestionFacturation", gestionFacturation);
            registry.rebind("GestionClient", gestionClient);

            System.out.println("Serveur prêt.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
