package SI.RH.entity;

/**
 * Entité POSTE — Bloc 1 : Structure organisationnelle.
 *
 * Un poste est un rôle métier défini, indépendant de toute personne qui l'occupe.
 * Règle de conception : POSTE ≠ EMPLOYE — le poste existe même si personne ne l'occupe (vacant).
 *
 * Table SQL : POSTE
 */
public class Poste {

    private int id;

    /** Code court unique (ex: "DEV-SR-01", "RH-MGR-02"). */
    private String codePoste;

    private String titre;

    private String descriptionMission;

    /** Fourchette basse du salaire mensuel brut en Ariary. */
    private Integer salaireMin;

    /** Fourchette haute du salaire mensuel brut en Ariary. Contrainte : salaireMax >= salaireMin. */
    private Integer salaireMax;

    /** Département auquel ce poste appartient. NOT NULL. */
    private int idDepartement;

    public Poste() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodePoste() {
        return codePoste;
    }

    public void setCodePoste(String codePoste) {
        this.codePoste = codePoste;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescriptionMission() {
        return descriptionMission;
    }

    public void setDescriptionMission(String descriptionMission) {
        this.descriptionMission = descriptionMission;
    }

    public Integer getSalaireMin() {
        return salaireMin;
    }

    public void setSalaireMin(Integer salaireMin) {
        this.salaireMin = salaireMin;
    }

    public Integer getSalaireMax() {
        return salaireMax;
    }

    public void setSalaireMax(Integer salaireMax) {
        this.salaireMax = salaireMax;
    }

    public int getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(int idDepartement) {
        this.idDepartement = idDepartement;
    }
}
