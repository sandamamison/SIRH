package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "ancien_poste_id", nullable = false)
    private Long ancienPosteId;
    @Column(name = "nouveau_poste_id", nullable = false)
    private Long nouveauPosteId;
    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;
    @Column(name = "augmentation_salaire", precision = 15, scale = 2)
    private BigDecimal augmentationSalaire = BigDecimal.ZERO;
    @Column(columnDefinition = "TEXT")
    private String motif;
    @Column(length = 30)
    private String statut = "VALIDEE";

    public Promotion() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getAncienPosteId() { return ancienPosteId; }
    public void setAncienPosteId(Long ancienPosteId) { this.ancienPosteId = ancienPosteId; }
    public Long getNouveauPosteId() { return nouveauPosteId; }
    public void setNouveauPosteId(Long nouveauPosteId) { this.nouveauPosteId = nouveauPosteId; }
    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }
    public BigDecimal getAugmentationSalaire() { return augmentationSalaire; }
    public void setAugmentationSalaire(BigDecimal augmentationSalaire) { this.augmentationSalaire = augmentationSalaire; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
