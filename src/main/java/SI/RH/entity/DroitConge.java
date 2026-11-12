package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "droit_conge",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employeId", "typeCongeId", "annee"}))
public class DroitConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Column(nullable = false)
    private Long typeCongeId;

    @Column(nullable = false)
    private Integer annee;

    @Column(nullable = false)
    private Double joursAcquis;

    public DroitConge() {}

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

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Double getJoursAcquis() {
        return joursAcquis;
    }

    public void setJoursAcquis(Double joursAcquis) {
        this.joursAcquis = joursAcquis;
    }
}
