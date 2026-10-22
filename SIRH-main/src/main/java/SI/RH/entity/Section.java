package SI.RH.entity;

import jakarta.persistence.*;

/**
 * Entité JPA Section — Bloc 5 : Structure organisationnelle (Departement > Service > Section).
 */
@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codeSection;

    @Column(nullable = false, length = 100)
    private String nomSection;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer serviceId;

    private Integer employeResponsableId;

    public Section() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeSection() {
        return codeSection;
    }

    public void setCodeSection(String codeSection) {
        this.codeSection = codeSection;
    }

    public String getNomSection() {
        return nomSection;
    }

    public void setNomSection(String nomSection) {
        this.nomSection = nomSection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getEmployeResponsableId() {
        return employeResponsableId;
    }

    public void setEmployeResponsableId(Integer employeResponsableId) {
        this.employeResponsableId = employeResponsableId;
    }
}
