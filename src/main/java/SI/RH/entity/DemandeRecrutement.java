package SI.RH.entity;

import SI.RH.entity.enums.MotifRecrutement;
import SI.RH.entity.enums.StatutDemande;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité JPA DemandeRecrutement — Bloc 1 : Expression du besoin en personnel.
 */
@Entity
@Table(name = "demandeRecrutement")
public class DemandeRecrutement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer posteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MotifRecrutement motif;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justification;

    private Integer employeRemplaceId; // RG1-03 : obligatoire ssi REMPLACEMENT

    @Column(nullable = false)
    private int effectifDemande = 1;

    @Column(nullable = false)
    private long budgetPrevu; // RG1-04 (BIGINT en BDD)

    @Column(nullable = false)
    private LocalDate dateSouhaitee; // RG1-04

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutDemande statut = StatutDemande.BROUILLON;

    @Column(nullable = false)
    private Integer employeDemandeurId; // RG1-01

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    void avantInsertion() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    public DemandeRecrutement() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPosteId() {
        return posteId;
    }

    public void setPosteId(Integer posteId) {
        this.posteId = posteId;
    }

    public MotifRecrutement getMotif() {
        return motif;
    }

    public void setMotif(MotifRecrutement motif) {
        this.motif = motif;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getEmployeRemplaceId() {
        return employeRemplaceId;
    }

    public void setEmployeRemplaceId(Integer employeRemplaceId) {
        this.employeRemplaceId = employeRemplaceId;
    }

    public int getEffectifDemande() {
        return effectifDemande;
    }

    public void setEffectifDemande(int effectifDemande) {
        this.effectifDemande = effectifDemande;
    }

    public long getBudgetPrevu() {
        return budgetPrevu;
    }

    public void setBudgetPrevu(long budgetPrevu) {
        this.budgetPrevu = budgetPrevu;
    }

    public LocalDate getDateSouhaitee() {
        return dateSouhaitee;
    }

    public void setDateSouhaitee(LocalDate dateSouhaitee) {
        this.dateSouhaitee = dateSouhaitee;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public Integer getEmployeDemandeurId() {
        return employeDemandeurId;
    }

    public void setEmployeDemandeurId(Integer employeDemandeurId) {
        this.employeDemandeurId = employeDemandeurId;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
