package SI.RH.entity;

import SI.RH.entity.enums.StatutDemande;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entité JPA HistoriqueStatutDemande — Bloc 1 : Traçabilité des transitions de statut.
 */
@Entity
@Table(name = "historiqueStatutDemande")
public class HistoriqueStatutDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer demandeRecrutementId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatutDemande ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutDemande nouveauStatut;

    @Column(nullable = false)
    private Integer employeActeurId;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateTransition;

    @PrePersist
    void avantInsertion() {
        if (dateTransition == null) {
            dateTransition = LocalDateTime.now();
        }
    }

    public HistoriqueStatutDemande() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDemandeRecrutementId() {
        return demandeRecrutementId;
    }

    public void setDemandeRecrutementId(Integer demandeRecrutementId) {
        this.demandeRecrutementId = demandeRecrutementId;
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

    public Integer getEmployeActeurId() {
        return employeActeurId;
    }

    public void setEmployeActeurId(Integer employeActeurId) {
        this.employeActeurId = employeActeurId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateTransition() {
        return dateTransition;
    }

    public void setDateTransition(LocalDateTime dateTransition) {
        this.dateTransition = dateTransition;
    }
}
