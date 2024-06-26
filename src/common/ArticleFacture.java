package common;

import java.io.Serializable;

public class ArticleFacture implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reference;
    private String famille;
    private double prixUnitaire;
    private int quantite;

    // Constructeurs, getters et setters
    public ArticleFacture(String reference, String famille, double prixUnitaire, int quantite) {
        this.reference = reference;
        this.famille = famille;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
    }

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

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
