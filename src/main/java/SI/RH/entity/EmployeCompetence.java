package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employe_competence")
public class EmployeCompetence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employe_id", nullable = false)
    private Long employeId;
    @Column(name = "competence_id", nullable = false)
    private Long competenceId;
    @Column(name = "niveau_acquis", nullable = false)
    private Integer niveauAcquis = 1;
    @Column(name = "niveau_requis", nullable = false)
    private Integer niveauRequis = 1;

    public EmployeCompetence() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getCompetenceId() { return competenceId; }
    public void setCompetenceId(Long competenceId) { this.competenceId = competenceId; }
    public Integer getNiveauAcquis() { return niveauAcquis; }
    public void setNiveauAcquis(Integer niveauAcquis) { this.niveauAcquis = niveauAcquis; }
    public Integer getNiveauRequis() { return niveauRequis; }
    public void setNiveauRequis(Integer niveauRequis) { this.niveauRequis = niveauRequis; }
}
