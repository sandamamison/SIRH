package SI.RH.entity;

import SI.RH.entity.enums.StatutCandidature;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historiquestatutcandidature")
public class HistoriqueStatutCandidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidatureid", nullable = false)
    private Long candidatureId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ancienstatut", length = 30)
    private StatutCandidature ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouveauStatut", nullable = false, length = 30)
    private StatutCandidature nouveauStatut;

    @Column(name = "employeacteurid", nullable = false)
    private Long employeActeurId;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "datetransition", nullable = false, updatable = false)
    private LocalDateTime dateTransition;

    public HistoriqueStatutCandidature() {}

    @PrePersist
    public void onCreate() {
        if (dateTransition == null) {
            dateTransition = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public Long getCandidatureId() { return candidatureId; }
    public void setCandidatureId(Long candidatureId) { this.candidatureId = candidatureId; }
    public StatutCandidature getAncienStatut() { return ancienStatut; }
    public void setAncienStatut(StatutCandidature ancienStatut) { this.ancienStatut = ancienStatut; }
    public StatutCandidature getNouveauStatut() { return nouveauStatut; }
    public void setNouveauStatut(StatutCandidature nouveauStatut) { this.nouveauStatut = nouveauStatut; }
    public Long getEmployeActeurId() { return employeActeurId; }
    public void setEmployeActeurId(Long employeActeurId) { this.employeActeurId = employeActeurId; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDateTime getDateTransition() { return dateTransition; }
}
