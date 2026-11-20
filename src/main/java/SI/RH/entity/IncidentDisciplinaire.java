package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "incident_disciplinaire")
public class IncidentDisciplinaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "date_incident", nullable = false)
    private LocalDate dateIncident;
    @Column(nullable = false)
    private String motif;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(length = 30)
    private String gravite = "MOYENNE"; // FAIBLE, MOYENNE, GRAVE, TRES_GRAVE
    @Column(length = 30)
    private String statut = "EN_COURS"; // DECLARE, EN_COURS, CLOTURE

    public IncidentDisciplinaire() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGravite() { return gravite; }
    public void setGravite(String gravite) { this.gravite = gravite; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
