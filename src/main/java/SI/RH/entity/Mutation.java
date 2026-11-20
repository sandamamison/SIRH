package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mutation")
public class Mutation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "ancien_departement_id")
    private Long ancienDepartementId;
    @Column(name = "nouveau_departement_id")
    private Long nouveauDepartementId;
    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;
    @Column(columnDefinition = "TEXT")
    private String motif;

    public Mutation() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getAncienDepartementId() { return ancienDepartementId; }
    public void setAncienDepartementId(Long ancienDepartementId) { this.ancienDepartementId = ancienDepartementId; }
    public Long getNouveauDepartementId() { return nouveauDepartementId; }
    public void setNouveauDepartementId(Long nouveauDepartementId) { this.nouveauDepartementId = nouveauDepartementId; }
    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
