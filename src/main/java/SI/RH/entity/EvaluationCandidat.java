package SI.RH.entity;

import SI.RH.entity.enums.AvisEvaluation;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "evaluationcandidat",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"entretienid", "employeevaluateurid"}
    )
)
public class EvaluationCandidat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entretienid", nullable = false)
    private Long entretienId;

    @Column(name = "employeevaluateurid", nullable = false)
    private Long employeEvaluateurId;

    @Column(name = "scoreglobal")
    private Integer scoreGlobal;

    @Column(name = "pointsforts", columnDefinition = "TEXT")
    private String pointsForts;

    @Column(name = "pointsfaibles", columnDefinition = "TEXT")
    private String pointsFaibles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AvisEvaluation avis;

    @Column(name = "dateevaluation", nullable = false, updatable = false)
    private LocalDateTime dateEvaluation;

    public EvaluationCandidat() {}

    @PrePersist
    public void avantCreation() {
        if (dateEvaluation == null) dateEvaluation = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEntretienId() { return entretienId; }
    public void setEntretienId(Long entretienId) { this.entretienId = entretienId; }

    public Long getEmployeEvaluateurId() { return employeEvaluateurId; }
    public void setEmployeEvaluateurId(Long employeEvaluateurId) { this.employeEvaluateurId = employeEvaluateurId; }

    public Integer getScoreGlobal() { return scoreGlobal; }
    public void setScoreGlobal(Integer scoreGlobal) { this.scoreGlobal = scoreGlobal; }

    public String getPointsForts() { return pointsForts; }
    public void setPointsForts(String pointsForts) { this.pointsForts = pointsForts; }

    public String getPointsFaibles() { return pointsFaibles; }
    public void setPointsFaibles(String pointsFaibles) { this.pointsFaibles = pointsFaibles; }

    public AvisEvaluation getAvis() { return avis; }
    public void setAvis(AvisEvaluation avis) { this.avis = avis; }

    public LocalDateTime getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDateTime dateEvaluation) { this.dateEvaluation = dateEvaluation; }
}
