package SI.RH.entity;

import SI.RH.entity.enums.DecisionValidation;
import SI.RH.entity.enums.NiveauValidation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entité JPA ValidationDemande — Bloc 1 : Traçabilité du circuit d'approbation.
 */
@Entity
@Table(name = "validationDemande")
public class ValidationDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer demandeRecrutementId;

    @Column(nullable = false)
    private Integer employeValideurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NiveauValidation niveau;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DecisionValidation decision;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateDecision;

    @PrePersist
    void avantInsertion() {
        if (dateDecision == null) {
            dateDecision = LocalDateTime.now();
        }
    }

    public ValidationDemande() {}

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

    public Integer getEmployeValideurId() {
        return employeValideurId;
    }

    public void setEmployeValideurId(Integer employeValideurId) {
        this.employeValideurId = employeValideurId;
    }

    public NiveauValidation getNiveau() {
        return niveau;
    }

    public void setNiveau(NiveauValidation niveau) {
        this.niveau = niveau;
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

    public LocalDateTime getDateDecision() {
        return dateDecision;
    }

    public void setDateDecision(LocalDateTime dateDecision) {
        this.dateDecision = dateDecision;
    }
}
