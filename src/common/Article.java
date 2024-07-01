package common;

import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private String reference;
    private String famille;
    private double prixUnitaire;
    private int stock;
    private String imageUrl;

    // Constructeurs, getters et setters
    public Article(String nom, String reference, String famille, double prixUnitaire, int stock, String imageUrl) {
        this.nom = nom;
        this.reference = reference;
        this.famille = famille;
        this.prixUnitaire = prixUnitaire;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public String getNom() {return nom;}

    public void setNom(String nom) {this.nom = nom;}

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFamille() {
        return famille;
    }

    public void setFamille(String famille) {
        this.famille = famille;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
}
