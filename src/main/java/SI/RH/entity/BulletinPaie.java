package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bulletin_paie")
public class BulletinPaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periode_paie_id", nullable = false)
    private Long periodePaieId;

    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @Column(name = "salaire_base", nullable = false, precision = 15, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "taux_journalier", nullable = false, precision = 15, scale = 2)
    private BigDecimal tauxJournalier;

    @Column(name = "taux_horaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal tauxHoraire;

    @Column(length = 20)
    private String classification = "HC";

    @Column(precision = 10, scale = 2)
    private BigDecimal indice = BigDecimal.valueOf(1733);

    @Column(name = "heures_normales", precision = 6, scale = 2)
    private BigDecimal heuresNormales = BigDecimal.valueOf(173.33);

    @Column(name = "total_hs", precision = 15, scale = 2)
    private BigDecimal totalHs = BigDecimal.ZERO;

    @Column(name = "total_absence_deductible", precision = 15, scale = 2)
    private BigDecimal totalAbsenceDeductible = BigDecimal.ZERO;

    @Column(name = "total_primes", precision = 15, scale = 2)
    private BigDecimal totalPrimes = BigDecimal.ZERO;

    @Column(name = "salaire_brut", nullable = false, precision = 15, scale = 2)
    private BigDecimal salaireBrut;

    @Column(name = "retenue_cnaps", nullable = false, precision = 15, scale = 2)
    private BigDecimal retenueCnaps;

    @Column(name = "retenue_sanitaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal retenueSanitaire;

    @Column(name = "total_irsa", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalIrsa;

    @Column(name = "total_retenues", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRetenues;

    @Column(name = "autres_indemnites", precision = 15, scale = 2)
    private BigDecimal autresIndemnites = BigDecimal.ZERO;

    @Column(name = "net_a_payer", nullable = false, precision = 15, scale = 2)
    private BigDecimal netAPayer;

    @Column(nullable = false, length = 20)
    private String statut = "VALIDE"; // BROUILLON, VALIDE, PAYE

    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration = LocalDateTime.now();

    @OneToMany(mappedBy = "bulletinPaieId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordre ASC")
    private List<LigneBulletin> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "bulletinPaieId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("numeroSemaine ASC")
    private List<CalculHsSemaine> calculHsSemaines = new ArrayList<>();

    public BulletinPaie() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPeriodePaieId() { return periodePaieId; }
    public void setPeriodePaieId(Long periodePaieId) { this.periodePaieId = periodePaieId; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public BigDecimal getSalaireBase() { return salaireBase; }
    public void setSalaireBase(BigDecimal salaireBase) { this.salaireBase = salaireBase; }

    public BigDecimal getTauxJournalier() { return tauxJournalier; }
    public void setTauxJournalier(BigDecimal tauxJournalier) { this.tauxJournalier = tauxJournalier; }

    public BigDecimal getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(BigDecimal tauxHoraire) { this.tauxHoraire = tauxHoraire; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public BigDecimal getIndice() { return indice; }
    public void setIndice(BigDecimal indice) { this.indice = indice; }

    public BigDecimal getHeuresNormales() { return heuresNormales; }
    public void setHeuresNormales(BigDecimal heuresNormales) { this.heuresNormales = heuresNormales; }

    public BigDecimal getTotalHs() { return totalHs; }
    public void setTotalHs(BigDecimal totalHs) { this.totalHs = totalHs; }

    public BigDecimal getTotalAbsenceDeductible() { return totalAbsenceDeductible; }
    public void setTotalAbsenceDeductible(BigDecimal totalAbsenceDeductible) { this.totalAbsenceDeductible = totalAbsenceDeductible; }

    public BigDecimal getTotalPrimes() { return totalPrimes; }
    public void setTotalPrimes(BigDecimal totalPrimes) { this.totalPrimes = totalPrimes; }

    public BigDecimal getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(BigDecimal salaireBrut) { this.salaireBrut = salaireBrut; }

    public BigDecimal getRetenueCnaps() { return retenueCnaps; }
    public void setRetenueCnaps(BigDecimal retenueCnaps) { this.retenueCnaps = retenueCnaps; }

    public BigDecimal getRetenueSanitaire() { return retenueSanitaire; }
    public void setRetenueSanitaire(BigDecimal retenueSanitaire) { this.retenueSanitaire = retenueSanitaire; }

    public BigDecimal getTotalIrsa() { return totalIrsa; }
    public void setTotalIrsa(BigDecimal totalIrsa) { this.totalIrsa = totalIrsa; }

    public BigDecimal getTotalRetenues() { return totalRetenues; }
    public void setTotalRetenues(BigDecimal totalRetenues) { this.totalRetenues = totalRetenues; }

    public BigDecimal getAutresIndemnites() { return autresIndemnites; }
    public void setAutresIndemnites(BigDecimal autresIndemnites) { this.autresIndemnites = autresIndemnites; }

    public BigDecimal getNetAPayer() { return netAPayer; }
    public void setNetAPayer(BigDecimal netAPayer) { this.netAPayer = netAPayer; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateGeneration() { return dateGeneration; }
    public void setDateGeneration(LocalDateTime dateGeneration) { this.dateGeneration = dateGeneration; }

    public List<LigneBulletin> getLignes() { return lignes; }
    public void setLignes(List<LigneBulletin> lignes) { this.lignes = lignes; }

    public List<CalculHsSemaine> getCalculHsSemaines() { return calculHsSemaines; }
    public void setCalculHsSemaines(List<CalculHsSemaine> calculHsSemaines) { this.calculHsSemaines = calculHsSemaines; }
}
