package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "regime_travail")
public class RegimeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String libelle;

    @Column(nullable = false)
    private Integer nbHeuresSemaine;

    public RegimeTravail() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getNbHeuresSemaine() {
        return nbHeuresSemaine;
    }

    public void setNbHeuresSemaine(Integer nbHeuresSemaine) {
        this.nbHeuresSemaine = nbHeuresSemaine;
    }
}
