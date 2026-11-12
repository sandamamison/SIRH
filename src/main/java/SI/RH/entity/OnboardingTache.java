package SI.RH.entity;

import SI.RH.entity.enums.StatutTache;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "onboarding_tache")
public class OnboardingTache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long onboardingId;

    @Column(nullable = false, length = 150)
    private String libelle;

    private Long responsableId;

    private LocalDate echeance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutTache statut = StatutTache.A_FAIRE;

    @Column(nullable = false)
    private Boolean obligatoire = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OnboardingTache() {}

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOnboardingId() {
        return onboardingId;
    }

    public void setOnboardingId(Long onboardingId) {
        this.onboardingId = onboardingId;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public LocalDate getEcheance() {
        return echeance;
    }

    public void setEcheance(LocalDate echeance) {
        this.echeance = echeance;
    }

    public StatutTache getStatut() {
        return statut;
    }

    public void setStatut(StatutTache statut) {
        this.statut = statut;
    }

    public Boolean getObligatoire() {
        return obligatoire;
    }

    public void setObligatoire(Boolean obligatoire) {
        this.obligatoire = obligatoire;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
