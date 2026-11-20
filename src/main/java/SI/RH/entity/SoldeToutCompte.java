package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "solde_tout_compte")
public class SoldeToutCompte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rupture_id", nullable = false)
    private Long ruptureId;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "date_calcul", nullable = false)
    private LocalDate dateCalcul;
    @Column(name = "salaire_dernier_mois", nullable = false, precision = 15, scale = 2)
    private BigDecimal salaireDernierMois;
    @Column(name = "indemnite_conges_payes", precision = 15, scale = 2)
    private BigDecimal indemniteCongesPayes = BigDecimal.ZERO;
    @Column(name = "indemnite_preavis", precision = 15, scale = 2)
    private BigDecimal indemnitePreavis = BigDecimal.ZERO;
    @Column(name = "indemnite_licenciement", precision = 15, scale = 2)
    private BigDecimal indemniteLicenciement = BigDecimal.ZERO;
    @Column(name = "total_brut", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalBrut;
    @Column(name = "total_retenues", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRetenues;
    @Column(name = "net_solde", nullable = false, precision = 15, scale = 2)
    private BigDecimal netSolde;
    @Column(name = "recu_signe")
    private Boolean recuSigne = false;

    public SoldeToutCompte() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRuptureId() { return ruptureId; }
    public void setRuptureId(Long ruptureId) { this.ruptureId = ruptureId; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public LocalDate getDateCalcul() { return dateCalcul; }
    public void setDateCalcul(LocalDate dateCalcul) { this.dateCalcul = dateCalcul; }
    public BigDecimal getSalaireDernierMois() { return salaireDernierMois; }
    public void setSalaireDernierMois(BigDecimal salaireDernierMois) { this.salaireDernierMois = salaireDernierMois; }
    public BigDecimal getIndemniteCongesPayes() { return indemniteCongesPayes; }
    public void setIndemniteCongesPayes(BigDecimal indemniteCongesPayes) { this.indemniteCongesPayes = indemniteCongesPayes; }
    public BigDecimal getIndemnitePreavis() { return indemnitePreavis; }
    public void setIndemnitePreavis(BigDecimal indemnitePreavis) { this.indemnitePreavis = indemnitePreavis; }
    public BigDecimal getIndemniteLicenciement() { return indemniteLicenciement; }
    public void setIndemniteLicenciement(BigDecimal indemniteLicenciement) { this.indemniteLicenciement = indemniteLicenciement; }
    public BigDecimal getTotalBrut() { return totalBrut; }
    public void setTotalBrut(BigDecimal totalBrut) { this.totalBrut = totalBrut; }
    public BigDecimal getTotalRetenues() { return totalRetenues; }
    public void setTotalRetenues(BigDecimal totalRetenues) { this.totalRetenues = totalRetenues; }
    public BigDecimal getNetSolde() { return netSolde; }
    public void setNetSolde(BigDecimal netSolde) { this.netSolde = netSolde; }
    public Boolean getRecuSigne() { return recuSigne; }
    public void setRecuSigne(Boolean recuSigne) { this.recuSigne = recuSigne; }
}
