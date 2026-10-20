package SI.RH.entity;

import jakarta.persistence.*;

/**
 * Entité JPA Departement — Bloc 1 : Structure organisationnelle.
 */
@Entity
@Table(name = "departement")
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codeDepartement;

    @Column(nullable = false, length = 100)
    private String nomDepartement;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer employeResponsableId;

    private Integer departementParentId;

    public Departement() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public String getNomDepartement() {
        return nomDepartement;
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEmployeResponsableId() {
        return employeResponsableId;
    }

    public void setEmployeResponsableId(Integer employeResponsableId) {
        this.employeResponsableId = employeResponsableId;
    }

    public Integer getDepartementParentId() {
        return departementParentId;
    }

    public void setDepartementParentId(Integer departementParentId) {
        this.departementParentId = departementParentId;
    }
}
