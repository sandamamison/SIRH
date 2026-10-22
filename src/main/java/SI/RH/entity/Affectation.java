package SI.RH.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entité JPA Affectation — Bloc 4 : Contrats et Affectations.
 *
 * RG4-04 : un employe ne peut avoir qu'une affectation active a la fois.
 * Un poste peut etre affecte a plusieurs employes successivement (historique).
 * La vacance d'un poste se deduit par requete (pas d'affectation active).
 */
@Entity
@Table(name = "affectation")
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer employeId;

    @Column(nullable = false)
    private Integer posteId;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    public Affectation() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getEmployeId() { return employeId; }
    public void setEmployeId(Integer employeId) { this.employeId = employeId; }

    public Integer getPosteId() { return posteId; }
    public void setPosteId(Integer posteId) { this.posteId = posteId; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}