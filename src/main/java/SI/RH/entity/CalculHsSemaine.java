package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "calcul_hs_semaine")
public class CalculHsSemaine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bulletin_paie_id", nullable = false)
    private Long bulletinPaieId;

    @Column(name = "numero_semaine", nullable = false)
    private Integer numeroSemaine;

    @Column(name = "heures_30", precision = 6, scale = 2)
    private BigDecimal heures30 = BigDecimal.ZERO;

    @Column(name = "heures_50", precision = 6, scale = 2)
    private BigDecimal heures50 = BigDecimal.ZERO;

    @Column(name = "heures_weekend", precision = 6, scale = 2)
    private BigDecimal heuresWeekend = BigDecimal.ZERO;

    @Column(name = "heures_ferie", precision = 6, scale = 2)
    private BigDecimal heuresFerie = BigDecimal.ZERO;

    @Column(name = "montant_30", precision = 15, scale = 2)
    private BigDecimal montant30 = BigDecimal.ZERO;

    @Column(name = "montant_50", precision = 15, scale = 2)
    private BigDecimal montant50 = BigDecimal.ZERO;

    @Column(name = "montant_weekend", precision = 15, scale = 2)
    private BigDecimal montantWeekend = BigDecimal.ZERO;

    @Column(name = "montant_ferie", precision = 15, scale = 2)
    private BigDecimal montantFerie = BigDecimal.ZERO;

    @Column(name = "total_heures", precision = 6, scale = 2)
    private BigDecimal totalHeures = BigDecimal.ZERO;

    @Column(name = "montant_semaine", precision = 15, scale = 2)
    private BigDecimal montantSemaine = BigDecimal.ZERO;

    public CalculHsSemaine() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBulletinPaieId() { return bulletinPaieId; }
    public void setBulletinPaieId(Long bulletinPaieId) { this.bulletinPaieId = bulletinPaieId; }

    public Integer getNumeroSemaine() { return numeroSemaine; }
    public void setNumeroSemaine(Integer numeroSemaine) { this.numeroSemaine = numeroSemaine; }

    public BigDecimal getHeures30() { return heures30; }
    public void setHeures30(BigDecimal heures30) { this.heures30 = heures30; }

    public BigDecimal getHeures50() { return heures50; }
    public void setHeures50(BigDecimal heures50) { this.heures50 = heures50; }

    public BigDecimal getHeuresWeekend() { return heuresWeekend; }
    public void setHeuresWeekend(BigDecimal heuresWeekend) { this.heuresWeekend = heuresWeekend; }

    public BigDecimal getHeuresFerie() { return heuresFerie; }
    public void setHeuresFerie(BigDecimal heuresFerie) { this.heuresFerie = heuresFerie; }

    public BigDecimal getMontant30() { return montant30; }
    public void setMontant30(BigDecimal montant30) { this.montant30 = montant30; }

    public BigDecimal getMontant50() { return montant50; }
    public void setMontant50(BigDecimal montant50) { this.montant50 = montant50; }

    public BigDecimal getMontantWeekend() { return montantWeekend; }
    public void setMontantWeekend(BigDecimal montantWeekend) { this.montantWeekend = montantWeekend; }

    public BigDecimal getMontantFerie() { return montantFerie; }
    public void setMontantFerie(BigDecimal montantFerie) { this.montantFerie = montantFerie; }

    public BigDecimal getTotalHeures() { return totalHeures; }
    public void setTotalHeures(BigDecimal totalHeures) { this.totalHeures = totalHeures; }

    public BigDecimal getMontantSemaine() { return montantSemaine; }
    public void setMontantSemaine(BigDecimal montantSemaine) { this.montantSemaine = montantSemaine; }
}
