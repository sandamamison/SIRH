package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "evaluation_rh")
public class EvaluationRh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "evaluateur_id", nullable = false)
    private Long evaluateurId;
    @Column(name = "date_evaluation", nullable = false)
    private LocalDate dateEvaluation;
    @Column(name = "note_globale", precision = 4, scale = 2)
    private BigDecimal noteGlobale;
    @Column(columnDefinition = "TEXT")
    private String commentaires;
    @Column(name = "besoins_formation", columnDefinition = "TEXT")
    private String besoinsFormation;
    @Column(length = 30)
    private String statut = "VALIDEE";

    public EvaluationRh() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getEvaluateurId() { return evaluateurId; }
    public void setEvaluateurId(Long evaluateurId) { this.evaluateurId = evaluateurId; }
    public LocalDate getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDate dateEvaluation) { this.dateEvaluation = dateEvaluation; }
    public BigDecimal getNoteGlobale() { return noteGlobale; }
    public void setNoteGlobale(BigDecimal noteGlobale) { this.noteGlobale = noteGlobale; }
    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
    public String getBesoinsFormation() { return besoinsFormation; }
    public void setBesoinsFormation(String besoinsFormation) { this.besoinsFormation = besoinsFormation; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
