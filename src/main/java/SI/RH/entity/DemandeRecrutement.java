package SI.RH.entity;

import SI.RH.entity.enums.MotifRecrutement;
import SI.RH.entity.enums.StatutDemande;

import java.util.Date;

/**
 * Entité DEMANDE_RECRUTEMENT — Bloc 1 : Expression du besoin en personnel.
 *
 * Un manager exprime un besoin de création ou de remplacement d'un poste.
 * La demande suit un workflow de validation : Manager → RH → Direction.
 *
 * Règles de gestion :
 *   RG1-02 : le motif est CREATION ou REMPLACEMENT.
 *   RG1-03 : si motif = REMPLACEMENT, idEmployeRemplace est obligatoire (et interdit sinon).
 *   RG1-04 : budget et date souhaitée sont obligatoires.
 *   RG1-07 : une demande VALIDEE_DIRECTION ou REJETEE n'est plus modifiable.
 *
 * Table SQL : DEMANDE_RECRUTEMENT
 */
public class DemandeRecrutement {

    private int id;

    /** Poste concerné par la demande (création ou remplacement). NOT NULL. */
    private int idPoste;

    /** RG1-02 : CREATION ou REMPLACEMENT. */
    private MotifRecrutement motif;

    /** Explication détaillée du besoin. NOT NULL. */
    private String justification;

    /**
     * RG1-03 : Référence l'employé à remplacer.
     * Obligatoire si motif = REMPLACEMENT, interdit si motif = CREATION.
     */
    private Integer idEmployeRemplace;

    /** Nombre de postes à pourvoir. Défaut : 1. Contrainte : > 0. */
    private int effectifDemande;

    /** Budget prévisionnel total en Ariary. Contrainte : > 0. */
    private int budgetPrevu;

    /** Date à laquelle le poste doit être pourvu. NOT NULL. */
    private Date dateSouhaitee;

    /** Statut courant dans le workflow. Défaut : BROUILLON. */
    private StatutDemande statut;

    /** Manager qui a émis la demande. Référence un Employe. NOT NULL. */
    private int idDemandeur;

    private Date dateCreation;

    public DemandeRecrutement() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPoste() {
        return idPoste;
    }

    public void setIdPoste(int idPoste) {
        this.idPoste = idPoste;
    }

    public MotifRecrutement getMotif() {
        return motif;
    }

    public void setMotif(MotifRecrutement motif) {
        this.motif = motif;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getIdEmployeRemplace() {
        return idEmployeRemplace;
    }

    public void setIdEmployeRemplace(Integer idEmployeRemplace) {
        this.idEmployeRemplace = idEmployeRemplace;
    }

    public int getEffectifDemande() {
        return effectifDemande;
    }

    public void setEffectifDemande(int effectifDemande) {
        this.effectifDemande = effectifDemande;
    }

    public int getBudgetPrevu() {
        return budgetPrevu;
    }

    public void setBudgetPrevu(int budgetPrevu) {
        this.budgetPrevu = budgetPrevu;
    }

    public Date getDateSouhaitee() {
        return dateSouhaitee;
    }

    public void setDateSouhaitee(Date dateSouhaitee) {
        this.dateSouhaitee = dateSouhaitee;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public int getIdDemandeur() {
        return idDemandeur;
    }

    public void setIdDemandeur(int idDemandeur) {
        this.idDemandeur = idDemandeur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}
