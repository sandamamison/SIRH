package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "historique_salaire")
public class HistoriqueSalaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "ancien_salaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal ancienSalaire;
    @Column(name = "nouveau_salaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal nouveauSalaire;
    @Column(name = "date_changement", nullable = false)
    private LocalDate dateChangement;
    @Column(length = 255)
    private String motif;

    public HistoriqueSalaire() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public BigDecimal getAncienSalaire() { return ancienSalaire; }
    public void setAncienSalaire(BigDecimal ancienSalaire) { this.ancienSalaire = ancienSalaire; }
    public BigDecimal getNouveauSalaire() { return nouveauSalaire; }
    public void setNouveauSalaire(BigDecimal nouveauSalaire) { this.nouveauSalaire = nouveauSalaire; }
    public LocalDate getDateChangement() { return dateChangement; }
    public void setDateChangement(LocalDate dateChangement) { this.dateChangement = dateChangement; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
