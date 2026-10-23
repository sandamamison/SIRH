package SI.RH.entity;

import SI.RH.entity.enums.StatutCandidature;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "candidature",
    uniqueConstraints = @UniqueConstraint(
        name = "candidature_offre_personne_unique",
        columnNames = {"offreemploiid", "personneid"}
    )
)
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offreemploiid", nullable = false)
    private Long offreEmploiId;

    @Column(name = "personneid", nullable = false)
    private Long personneId;

    @Column(name = "datecandidature", nullable = false, updatable = false)
    private LocalDateTime dateCandidature;

    @Column(name = "cvurl", length = 255)
    private String cvUrl;

    @Column(name = "lettremotivation", columnDefinition = "TEXT")
    private String lettreMotivation;

    @Column(name = "pretentionsalariale")
    private Integer pretentionSalariale;

    @Column(name = "accordsalaire")
    private Boolean accordSalaire;

    @Column(name = "remarquesalaire", columnDefinition = "TEXT")
    private String remarqueSalaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutCandidature statut = StatutCandidature.RECUE;

    public Candidature() {}

    @PrePersist
    public void onCreate() {
        if (dateCandidature == null) {
            dateCandidature = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutCandidature.RECUE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOffreEmploiId() {
        return offreEmploiId;
    }

    public void setOffreEmploiId(Long offreEmploiId) {
        this.offreEmploiId = offreEmploiId;
    }

    public Long getPersonneId() {
        return personneId;
    }

    public void setPersonneId(Long personneId) {
        this.personneId = personneId;
    }

    public LocalDateTime getDateCandidature() {
        return dateCandidature;
    }

    public void setDateCandidature(LocalDateTime dateCandidature) {
        this.dateCandidature = dateCandidature;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getLettreMotivation() {
        return lettreMotivation;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    public Integer getPretentionSalariale() {
        return pretentionSalariale;
    }

    public void setPretentionSalariale(Integer pretentionSalariale) {
        this.pretentionSalariale = pretentionSalariale;
    }

    public Boolean getAccordSalaire() {
        return accordSalaire;
    }

    public void setAccordSalaire(Boolean accordSalaire) {
        this.accordSalaire = accordSalaire;
    }

    public String getRemarqueSalaire() {
        return remarqueSalaire;
    }

    public void setRemarqueSalaire(String remarqueSalaire) {
        this.remarqueSalaire = remarqueSalaire;
    }

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }
}
