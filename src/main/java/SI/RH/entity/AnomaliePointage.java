package SI.RH.entity;

import SI.RH.entity.enums.StatutAnomalie;
import SI.RH.entity.enums.TypeAnomalie;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomalie_pointage")
public class AnomaliePointage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    private Long pointageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TypeAnomalie typeAnomalie;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutAnomalie statut = StatutAnomalie.DETECTEE;

    private String commentaireJustification;

    @Column(nullable = false, updatable = false)
    private LocalDateTime detectedAt;

    public AnomaliePointage() {}

    @PrePersist
    void onCreate() {
        detectedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getPointageId() {
        return pointageId;
    }

    public void setPointageId(Long pointageId) {
        this.pointageId = pointageId;
    }

    public TypeAnomalie getTypeAnomalie() {
        return typeAnomalie;
    }

    public void setTypeAnomalie(TypeAnomalie typeAnomalie) {
        this.typeAnomalie = typeAnomalie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutAnomalie getStatut() {
        return statut;
    }

    public void setStatut(StatutAnomalie statut) {
        this.statut = statut;
    }

    public String getCommentaireJustification() {
        return commentaireJustification;
    }

    public void setCommentaireJustification(String commentaireJustification) {
        this.commentaireJustification = commentaireJustification;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
}
