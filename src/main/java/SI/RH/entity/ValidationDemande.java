package SI.RH.entity;

import SI.RH.entity.enums.DecisionValidation;
import SI.RH.entity.enums.RoleUtilisateur;

import java.util.Date;

/**
 * Entité VALIDATION_DEMANDE — Bloc 1 : Traçabilité du circuit d'approbation.
 *
 * Chaque étape du workflow (MANAGER → RH → DIRECTION) génère un enregistrement
 * immuable dans cette table.
 *
 * Règles de gestion :
 *   RG1-05 : l'ordre de validation est RH puis Direction.
 *   RG1-06 : chaque décision est tracée (qui, quand, commentaire).
 *   Contrainte SQL : une seule décision par (id_demande, niveau_role).
 *
 * Table SQL : VALIDATION_DEMANDE
 */
public class ValidationDemande {

    private int id;

    /** Demande concernée par cette validation. NOT NULL. */
    private int idDemande;

    /**
     * Salarié qui a pris la décision.
     * Référence un Employe ayant le rôle correspondant à niveauRole.
     */
    private int idValideur;

    /** Niveau hiérarchique de la validation : MANAGER, RH ou DIRECTION. */
    private RoleUtilisateur niveauRole;

    /** APPROUVE ou REJETE. */
    private DecisionValidation decision;

    /** Commentaire libre justifiant la décision (optionnel). */
    private String commentaire;

    private Date dateDecision;

    public ValidationDemande() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public int getIdValideur() {
        return idValideur;
    }

    public void setIdValideur(int idValideur) {
        this.idValideur = idValideur;
    }

    public RoleUtilisateur getNiveauRole() {
        return niveauRole;
    }

    public void setNiveauRole(RoleUtilisateur niveauRole) {
        this.niveauRole = niveauRole;
    }

    public DecisionValidation getDecision() {
        return decision;
    }

    public void setDecision(DecisionValidation decision) {
        this.decision = decision;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getDateDecision() {
        return dateDecision;
    }

    public void setDateDecision(Date dateDecision) {
        this.dateDecision = dateDecision;
    }
}
