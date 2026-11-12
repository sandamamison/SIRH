package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "planning",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employeId", "semaineDebut"}))
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Column(nullable = false)
    private Long horaireId;

    @Column(nullable = false)
    private LocalDate semaineDebut;

    private Long regimeTravailId;

    public Planning() {}

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

    public Long getHoraireId() {
        return horaireId;
    }

    public void setHoraireId(Long horaireId) {
        this.horaireId = horaireId;
    }

    public LocalDate getSemaineDebut() {
        return semaineDebut;
    }

    public void setSemaineDebut(LocalDate semaineDebut) {
        this.semaineDebut = semaineDebut;
    }

    public Long getRegimeTravailId() {
        return regimeTravailId;
    }

    public void setRegimeTravailId(Long regimeTravailId) {
        this.regimeTravailId = regimeTravailId;
    }
}
