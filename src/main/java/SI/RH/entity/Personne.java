package SI.RH.entity;

import java.util.Date;

public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private Date created_at;
    private Date updated_at;

    public Personne(){}

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getAdresse() {
        return adresse;
    }

    public Date getCreated_at() {
        return created_at;
    }
    public String getTelephone(){
        return telephone;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
