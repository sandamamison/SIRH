package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horaire")
public class Horaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String libelle;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    private Integer pauseMinutes = 0;

    public Horaire() {}

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

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public Integer getPauseMinutes() {
        return pauseMinutes;
    }

    public void setPauseMinutes(Integer pauseMinutes) {
        this.pauseMinutes = pauseMinutes;
    }
}
