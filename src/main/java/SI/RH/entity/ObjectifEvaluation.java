package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "objectif_evaluation")
public class ObjectifEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(nullable = false)
    private String titre;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(length = 100)
    private String cible;
    @Column(length = 30)
    private String statut = "EN_COURS";

    public ObjectifEvaluation() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCible() { return cible; }
    public void setCible(String cible) { this.cible = cible; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
