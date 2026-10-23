package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "intervenantentretien",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"entretienid", "employeid"}
    )
)
public class IntervenantEntretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entretienid", nullable = false)
    private Long entretienId;

    @Column(name = "employeid", nullable = false)
    private Long employeId;

    @Column(name = "roleintervenant", nullable = false, length = 50)
    private String roleIntervenant = "EXAMINATEUR";

    public IntervenantEntretien() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEntretienId() { return entretienId; }
    public void setEntretienId(Long entretienId) { this.entretienId = entretienId; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public String getRoleIntervenant() { return roleIntervenant; }
    public void setRoleIntervenant(String roleIntervenant) { this.roleIntervenant = roleIntervenant; }
}
