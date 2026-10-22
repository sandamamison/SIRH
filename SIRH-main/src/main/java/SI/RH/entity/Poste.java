package SI.RH.entity;

import jakarta.persistence.*;

/**
 * Entité JPA Poste — Bloc 1 : Structure organisationnelle.
 */
@Entity
@Table(name = "poste")
public class Poste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String codePoste;

    @Column(nullable = false, length = 100)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String descriptionMission;

    private Integer salaireMin;

    private Integer salaireMax;

    @Column(nullable = false)
    private Integer departementId;

    public Poste() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodePoste() {
        return codePoste;
    }

    public void setCodePoste(String codePoste) {
        this.codePoste = codePoste;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescriptionMission() {
        return descriptionMission;
    }

    public void setDescriptionMission(String descriptionMission) {
        this.descriptionMission = descriptionMission;
    }

    public Integer getSalaireMin() {
        return salaireMin;
    }

    public void setSalaireMin(Integer salaireMin) {
        this.salaireMin = salaireMin;
    }

    public Integer getSalaireMax() {
        return salaireMax;
    }

    public void setSalaireMax(Integer salaireMax) {
        this.salaireMax = salaireMax;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }
}
