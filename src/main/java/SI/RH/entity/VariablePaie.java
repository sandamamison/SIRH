package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "variable_paie")
public class VariablePaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periode_paie_id", nullable = false)
    private Long periodePaieId;

    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @Column(nullable = false, length = 50)
    private String type; // PRIME_RENDEMENT, PRIME_ANCIENNETE, HS, ABSENCE, RAPPEL, AUTRES_INDEMNITES

    @Column(nullable = false)
    private String libelle;

    @Column(precision = 10, scale = 2)
    private BigDecimal nombre = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal taux = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant = BigDecimal.ZERO;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public VariablePaie() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPeriodePaieId() { return periodePaieId; }
    public void setPeriodePaieId(Long periodePaieId) { this.periodePaieId = periodePaieId; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public BigDecimal getNombre() { return nombre; }
    public void setNombre(BigDecimal nombre) { this.nombre = nombre; }

    public BigDecimal getTaux() { return taux; }
    public void setTaux(BigDecimal taux) { this.taux = taux; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
