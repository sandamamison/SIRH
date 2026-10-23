package SI.RH.entity;

import SI.RH.entity.enums.StatutEntretien;
import SI.RH.entity.enums.TypeEntretien;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entretien")
public class Entretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidatureid", nullable = false)
    private Long candidatureId;

    @Column(name = "dateheure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "lieuoulien", length = 255)
    private String lieuOuLien;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeentretien", nullable = false, length = 30)
    private TypeEntretien typeEntretien = TypeEntretien.RH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEntretien statut = StatutEntretien.PLANIFIE;

    public Entretien() {}

    @PrePersist
    public void avantCreation() {
        if (typeEntretien == null) typeEntretien = TypeEntretien.RH;
        if (statut == null) statut = StatutEntretien.PLANIFIE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCandidatureId() { return candidatureId; }
    public void setCandidatureId(Long candidatureId) { this.candidatureId = candidatureId; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public String getLieuOuLien() { return lieuOuLien; }
    public void setLieuOuLien(String lieuOuLien) { this.lieuOuLien = lieuOuLien; }

    public TypeEntretien getTypeEntretien() { return typeEntretien; }
    public void setTypeEntretien(TypeEntretien typeEntretien) { this.typeEntretien = typeEntretien; }

    public StatutEntretien getStatut() { return statut; }
    public void setStatut(StatutEntretien statut) { this.statut = statut; }
}
