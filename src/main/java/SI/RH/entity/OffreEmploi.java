package SI.RH.entity;

import SI.RH.entity.enums.StatutOffre;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "offreEmploi")
public class OffreEmploi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "demandeRecrutementId", nullable = false, unique = true)
    private Integer demandeRecrutementId;

    @Column(nullable = false, length = 150)
    private String titreOffre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private Integer salairePropose;

    @Column(length = 100)
    private String horairesTravail;

    @Column(nullable = false, length = 150)
    private String lieuTravail;

    private LocalDate datePublication;

    private LocalDate dateCloture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutOffre statut = StatutOffre.BROUILLON;

    public OffreEmploi() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDemandeRecrutementId() {
        return demandeRecrutementId;
    }

    public void setDemandeRecrutementId(Integer demandeRecrutementId) {
        this.demandeRecrutementId = demandeRecrutementId;
    }

    public String getTitreOffre() {
        return titreOffre;
    }

    public void setTitreOffre(String titreOffre) {
        this.titreOffre = titreOffre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSalairePropose() {
        return salairePropose;
    }

    public void setSalairePropose(Integer salairePropose) {
        this.salairePropose = salairePropose;
    }

    public String getHorairesTravail() {
        return horairesTravail;
    }

    public void setHorairesTravail(String horairesTravail) {
        this.horairesTravail = horairesTravail;
    }

    public String getLieuTravail() {
        return lieuTravail;
    }

    public void setLieuTravail(String lieuTravail) {
        this.lieuTravail = lieuTravail;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    public LocalDate getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDate dateCloture) {
        this.dateCloture = dateCloture;
    }

    public StatutOffre getStatut() {
        return statut;
    }

    public void setStatut(StatutOffre statut) {
        this.statut = statut;
    }
}