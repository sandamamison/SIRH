package SI.RH.entity;

import SI.RH.entity.enums.TypeAbsence;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "absence")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeAbsence typeAbsence;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    private Long demandeCongeId;

    private String justificatifUrl;

    @Column(nullable = false)
    private Boolean justifiee = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Absence() {}

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

    public TypeAbsence getTypeAbsence() {
        return typeAbsence;
    }

    public void setTypeAbsence(TypeAbsence typeAbsence) {
        this.typeAbsence = typeAbsence;
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

    public Long getDemandeCongeId() {
        return demandeCongeId;
    }

    public void setDemandeCongeId(Long demandeCongeId) {
        this.demandeCongeId = demandeCongeId;
    }

    public String getJustificatifUrl() {
        return justificatifUrl;
    }

    public void setJustificatifUrl(String justificatifUrl) {
        this.justificatifUrl = justificatifUrl;
    }

    public Boolean getJustifiee() {
        return justifiee;
    }

    public void setJustifiee(Boolean justifiee) {
        this.justifiee = justifiee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
