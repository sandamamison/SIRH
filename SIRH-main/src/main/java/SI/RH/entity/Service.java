package SI.RH.entity;

import jakarta.persistence.*;

/**
 * Entité JPA Service — Bloc 5 : Structure organisationnelle (Departement > Service > Section).
 */
@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codeService;

    @Column(nullable = false, length = 100)
    private String nomService;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer departementId;

    private Integer employeResponsableId;

    public Service() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeService() {
        return codeService;
    }

    public void setCodeService(String codeService) {
        this.codeService = codeService;
    }

    public String getNomService() {
        return nomService;
    }

    public void setNomService(String nomService) {
        this.nomService = nomService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }

    public Integer getEmployeResponsableId() {
        return employeResponsableId;
    }

    public void setEmployeResponsableId(Integer employeResponsableId) {
        this.employeResponsableId = employeResponsableId;
    }
}
