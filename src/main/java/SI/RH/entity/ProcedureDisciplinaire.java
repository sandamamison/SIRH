package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "procedure_disciplinaire")
public class ProcedureDisciplinaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "incident_id", nullable = false)
    private Long incidentId;
    @Column(name = "date_demande_explication", nullable = false)
    private LocalDate dateDemandeExplication;
    @Column(name = "demande_explication", nullable = false, columnDefinition = "TEXT")
    private String demandeExplication;
    @Column(name = "date_reponse")
    private LocalDate dateReponse;
    @Column(name = "reponse_salarie", columnDefinition = "TEXT")
    private String reponseSalarie;
    @Column(name = "date_decision")
    private LocalDate dateDecision;
    @Column(columnDefinition = "TEXT")
    private String decision;
    @Column(length = 30)
    private String statut = "DEMANDE_ENVOYEE"; // DEMANDE_ENVOYEE, REPONSE_RECUE, DECIDEE, CLOTUREE

    public ProcedureDisciplinaire() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }
    public LocalDate getDateDemandeExplication() { return dateDemandeExplication; }
    public void setDateDemandeExplication(LocalDate dateDemandeExplication) { this.dateDemandeExplication = dateDemandeExplication; }
    public String getDemandeExplication() { return demandeExplication; }
    public void setDemandeExplication(String demandeExplication) { this.demandeExplication = demandeExplication; }
    public LocalDate getDateReponse() { return dateReponse; }
    public void setDateReponse(LocalDate dateReponse) { this.dateReponse = dateReponse; }
    public String getReponseSalarie() { return reponseSalarie; }
    public void setReponseSalarie(String reponseSalarie) { this.reponseSalarie = reponseSalarie; }
    public LocalDate getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDate dateDecision) { this.dateDecision = dateDecision; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
