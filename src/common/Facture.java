package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Facture implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int clientId;
    private double total;
    private String modePaiement;
    private LocalDate dateFacturation;
    private List<ArticleFacture> articles;

    // Constructeurs, getters et setters
    public Facture(int id, int clientId, double total, String modePaiement, LocalDate dateFacturation, List<ArticleFacture> articles) {
        this.id = id;
        this.clientId = clientId;
        this.total = total;
        this.modePaiement = modePaiement;
        this.dateFacturation = dateFacturation;
        this.articles = articles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public LocalDate getDateFacturation() {
        return dateFacturation;
    }

    public void setDateFacturation(LocalDate dateFacturation) {
        this.dateFacturation = dateFacturation;
    }

    public List<ArticleFacture> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleFacture> articles) {
        this.articles = articles;
    }
}
