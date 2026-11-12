package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "type_conge")
public class TypeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String libelle;

    @Column(nullable = false)
    private Boolean plafonne = false;

    private Integer dureeMaxJours;

    public TypeConge() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Boolean getPlafonne() {
        return plafonne;
    }

    public void setPlafonne(Boolean plafonne) {
        this.plafonne = plafonne;
    }

    public Integer getDureeMaxJours() {
        return dureeMaxJours;
    }

    public void setDureeMaxJours(Integer dureeMaxJours) {
        this.dureeMaxJours = dureeMaxJours;
    }
}
