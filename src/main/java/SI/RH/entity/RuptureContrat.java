package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rupture_contrat")
public class RuptureContrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "contrat_id", nullable = false)
    private Long contratId;
    @Column(nullable = false, length = 50)
    private String motif; // DEMISSION, LICENCIEMENT, FIN_CDD, RETRAITE, RUPTURE_CONVENTIONNELLE
    @Column(name = "date_notification", nullable = false)
    private LocalDate dateNotification;
    @Column(name = "date_sortie_prevue", nullable = false)
    private LocalDate dateSortiePrevue;
    @Column(name = "date_sortie_effective")
    private LocalDate dateSortieEffective;
    @Column(name = "preavis_effectue")
    private Boolean preavisEffectue = true;
    @Column(name = "jours_preavis")
    private Integer joursPreavis = 30;
    @Column(name = "jours_conges_restants", precision = 5, scale = 2)
    private BigDecimal joursCongesRestants = BigDecimal.ZERO;
    @Column(length = 30)
    private String statut = "EN_COURS"; // INITIÉE, PREAVIS_EN_COURS, TERMINEE

    public RuptureContrat() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getContratId() { return contratId; }
    public void setContratId(Long contratId) { this.contratId = contratId; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public LocalDate getDateNotification() { return dateNotification; }
    public void setDateNotification(LocalDate dateNotification) { this.dateNotification = dateNotification; }
    public LocalDate getDateSortiePrevue() { return dateSortiePrevue; }
    public void setDateSortiePrevue(LocalDate dateSortiePrevue) { this.dateSortiePrevue = dateSortiePrevue; }
    public LocalDate getDateSortieEffective() { return dateSortieEffective; }
    public void setDateSortieEffective(LocalDate dateSortieEffective) { this.dateSortieEffective = dateSortieEffective; }
    public Boolean getPreavisEffectue() { return preavisEffectue; }
    public void setPreavisEffectue(Boolean preavisEffectue) { this.preavisEffectue = preavisEffectue; }
    public Integer getJoursPreavis() { return joursPreavis; }
    public void setJoursPreavis(Integer joursPreavis) { this.joursPreavis = joursPreavis; }
    public BigDecimal getJoursCongesRestants() { return joursCongesRestants; }
    public void setJoursCongesRestants(BigDecimal joursCongesRestants) { this.joursCongesRestants = joursCongesRestants; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
