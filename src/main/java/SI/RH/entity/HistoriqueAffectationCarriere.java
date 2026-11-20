package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "historique_affectation_carriere")
public class HistoriqueAffectationCarriere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "poste_id", nullable = false)
    private Long posteId;
    @Column(name = "departement_id")
    private Long departementId;
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    @Column(name = "date_fin")
    private LocalDate dateFin;
    @Column(length = 100)
    private String motif;

    public HistoriqueAffectationCarriere() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getPosteId() { return posteId; }
    public void setPosteId(Long posteId) { this.posteId = posteId; }
    public Long getDepartementId() { return departementId; }
    public void setDepartementId(Long departementId) { this.departementId = departementId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
