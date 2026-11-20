package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "formation")
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String titre;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "duree_heures", nullable = false)
    private Integer dureeHeures;
    @Column(precision = 15, scale = 2)
    private BigDecimal cout = BigDecimal.ZERO;
    @Column(name = "employe_id")
    private Long employeId;
    @Column(name = "date_debut")
    private LocalDate dateDebut;
    @Column(name = "date_fin")
    private LocalDate dateFin;
    @Column(length = 30)
    private String statut = "PLANIFIEE";
    @Column(name = "evaluation_post_formation", columnDefinition = "TEXT")
    private String evaluationPostFormation;

    public Formation() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDureeHeures() { return dureeHeures; }
    public void setDureeHeures(Integer dureeHeures) { this.dureeHeures = dureeHeures; }
    public BigDecimal getCout() { return cout; }
    public void setCout(BigDecimal cout) { this.cout = cout; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getEvaluationPostFormation() { return evaluationPostFormation; }
    public void setEvaluationPostFormation(String evaluationPostFormation) { this.evaluationPostFormation = evaluationPostFormation; }
}
