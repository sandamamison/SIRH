package SI.RH.entity;

import java.util.Date;

/**
 * Entité DEPARTEMENT — Bloc 1 : Structure organisationnelle.
 *
 * Un département est une unité organisationnelle de l'entreprise.
 * Il peut être hiérarchisé (département parent) et avoir un responsable salarié.
 *
 * Table SQL : DEPARTEMENT
 */
public class Departement {

    private int id;

    /** Code court unique du département (ex: "IT", "RH", "FIN"). */
    private String codeDepartement;

    private String nomDepartement;

    private String description;

    /**
     * Responsable du département.
     * Référence l'id d'un Employe (clé étrangère résolue via ALTER TABLE côté SQL).
     */
    private Integer idResponsable;

    /**
     * Référence le département parent pour modéliser la hiérarchie organisationnelle
     * (sections, services, départements).
     * NULL si le département est à la racine.
     */
    private Integer idDepartementParent;

    public Departement() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public String getNomDepartement() {
        return nomDepartement;
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Integer getIdDepartementParent() {
        return idDepartementParent;
    }

    public void setIdDepartementParent(Integer idDepartementParent) {
        this.idDepartementParent = idDepartementParent;
    }
}
