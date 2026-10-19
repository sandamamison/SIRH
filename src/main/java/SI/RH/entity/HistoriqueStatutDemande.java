package SI.RH.entity;

import SI.RH.entity.enums.StatutDemande;

import java.util.Date;

/**
 * Entité HISTORIQUE_STATUT_DEMANDE — Traçabilité des transitions de statut.
 *
 * Chaque changement de statut d'une demande de recrutement est enregistré ici
 * avec l'acteur, la date et un commentaire optionnel.
 *
 * Règle : RG1-06 / Audit 2 Défaut 4 — "qui a fait quoi, quand" est obligatoire.
 *
 * Table SQL : HISTORIQUE_STATUT_DEMANDE
 */
public class HistoriqueStatutDemande {

    private int id;

    private int idDemande;

    /** Statut avant la transition. NULL pour la création initiale. */
    private StatutDemande ancienStatut;

    /** Nouveau statut après la transition. NOT NULL. */
    private StatutDemande nouveauStatut;

    /** Salarié auteur du changement. Référence un Employe. NOT NULL. */
    private int idActeur;

    private String commentaire;

    private Date dateTransition;

    public HistoriqueStatutDemande() {}

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

    public StatutDemande getAncienStatut() {
        return ancienStatut;
    }

    public void setAncienStatut(StatutDemande ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public StatutDemande getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(StatutDemande nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public int getIdActeur() {
        return idActeur;
    }

    public void setIdActeur(int idActeur) {
        this.idActeur = idActeur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getDateTransition() {
        return dateTransition;
    }

    public void setDateTransition(Date dateTransition) {
        this.dateTransition = dateTransition;
    }
}
