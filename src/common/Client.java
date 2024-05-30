package common;

import java.io.Serializable;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L; // Ajoute un identifiant de version

    private int id;
    private String nom;
    private String email;

    // Constructeurs, getters et setters
    public Client(int id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
