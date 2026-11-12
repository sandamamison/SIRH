package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solde_conge",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "type_conge_id"}))
public class SoldeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @Column(name = "type_conge_id", nullable = false)
    private Long typeCongeId;

    @Column(nullable = false)
    private Double soldeCourant = 0.0;

    private LocalDateTime updatedAt;

    public SoldeConge() {}

    @PrePersist
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getTypeCongeId() {
        return typeCongeId;
    }

    public void setTypeCongeId(Long typeCongeId) {
        this.typeCongeId = typeCongeId;
    }

    public Double getSoldeCourant() {
        return soldeCourant;
    }

    public void setSoldeCourant(Double soldeCourant) {
        this.soldeCourant = soldeCourant;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
