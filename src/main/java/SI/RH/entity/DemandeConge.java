package SI.RH.entity;

import SI.RH.entity.enums.StatutDemandeConge;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_conge")
public class DemandeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Column(nullable = false)
    private Long typeCongeId;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatutDemandeConge statut = StatutDemandeConge.BROUILLON;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private Long validateurManagerId;
    private LocalDateTime dateValidationManager;
    private Long validateurRhId;
    private LocalDateTime dateValidationRh;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DemandeConge() {}

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

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getTypeCongeId() {
        return typeCongeId;
    }

    public void setTypeCongeId(Long typeCongeId) {
        this.typeCongeId = typeCongeId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public StatutDemandeConge getStatut() {
        return statut;
    }

    public void setStatut(StatutDemandeConge statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Long getValidateurManagerId() {
        return validateurManagerId;
    }

    public void setValidateurManagerId(Long validateurManagerId) {
        this.validateurManagerId = validateurManagerId;
    }

    public LocalDateTime getDateValidationManager() {
        return dateValidationManager;
    }

    public void setDateValidationManager(LocalDateTime dateValidationManager) {
        this.dateValidationManager = dateValidationManager;
    }

    public Long getValidateurRhId() {
        return validateurRhId;
    }

    public void setValidateurRhId(Long validateurRhId) {
        this.validateurRhId = validateurRhId;
    }

    public LocalDateTime getDateValidationRh() {
        return dateValidationRh;
    }

    public void setDateValidationRh(LocalDateTime dateValidationRh) {
        this.dateValidationRh = dateValidationRh;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
