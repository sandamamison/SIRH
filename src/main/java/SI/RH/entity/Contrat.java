package SI.RH.entity;

import SI.RH.entity.enums.StatutContrat;
import SI.RH.entity.enums.TypeContrat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité JPA Contrat — Bloc 4 : Contrats et Affectations.
 *
 * Regles metier :
 *  - RG4-01 : CDI <=> dateFin null (CE/CDD ont obligatoirement une dateFin).
 *  - RG4-02 : un employe ne peut avoir deux contrats actifs en meme temps
 *             (garanti par contrainte EXCLUDE en BDD + verif applicative).
 *  - RG4-03 : un renouvellement cree un NOUVEAU contrat lie au precedent
 *             via {@code contratPrecedentId}. Historique jamais ecrase.
 */
@Entity
@Table(name = "contrat")
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroContrat;

    @Column(nullable = false)
    private Integer employeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TypeContrat typeContrat = TypeContrat.CE;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Column(nullable = false)
    private Integer salaireBrut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutContrat statut = StatutContrat.CREE;

    private Integer contratPrecedentId;

    @Column(nullable = false)
    private Integer employeCreateurId;

    private Integer employeSignataireId;

    private LocalDate dateSignature;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    void avantInsertion() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    public Contrat() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumeroContrat() { return numeroContrat; }
    public void setNumeroContrat(String numeroContrat) { this.numeroContrat = numeroContrat; }

    public Integer getEmployeId() { return employeId; }
    public void setEmployeId(Integer employeId) { this.employeId = employeId; }

    public TypeContrat getTypeContrat() { return typeContrat; }
    public void setTypeContrat(TypeContrat typeContrat) { this.typeContrat = typeContrat; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Integer getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(Integer salaireBrut) { this.salaireBrut = salaireBrut; }

    public StatutContrat getStatut() { return statut; }
    public void setStatut(StatutContrat statut) { this.statut = statut; }

    public Integer getContratPrecedentId() { return contratPrecedentId; }
    public void setContratPrecedentId(Integer contratPrecedentId) { this.contratPrecedentId = contratPrecedentId; }

    public Integer getEmployeCreateurId() { return employeCreateurId; }
    public void setEmployeCreateurId(Integer employeCreateurId) { this.employeCreateurId = employeCreateurId; }

    public Integer getEmployeSignataireId() { return employeSignataireId; }
    public void setEmployeSignataireId(Integer employeSignataireId) { this.employeSignataireId = employeSignataireId; }

    public LocalDate getDateSignature() { return dateSignature; }
    public void setDateSignature(LocalDate dateSignature) { this.dateSignature = dateSignature; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
