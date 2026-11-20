package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sanction_disciplinaire")
public class SanctionDisciplinaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "procedure_id", nullable = false)
    private Long procedureId;
    @Column(name = "type_sanction", nullable = false, length = 50)
    private String typeSanction; // AVERTISSEMENT_VERBAL, AVERTISSEMENT_ECRIT, MISE_A_PIED, RETROGRADATION, LICENCIEMENT_DISCIPLINAIRE
    @Column(name = "date_notification", nullable = false)
    private LocalDate dateNotification;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "justificatif_url")
    private String justificatifUrl;

    public SanctionDisciplinaire() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProcedureId() { return procedureId; }
    public void setProcedureId(Long procedureId) { this.procedureId = procedureId; }
    public String getTypeSanction() { return typeSanction; }
    public void setTypeSanction(String typeSanction) { this.typeSanction = typeSanction; }
    public LocalDate getDateNotification() { return dateNotification; }
    public void setDateNotification(LocalDate dateNotification) { this.dateNotification = dateNotification; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getJustificatifUrl() { return justificatifUrl; }
    public void setJustificatifUrl(String justificatifUrl) { this.justificatifUrl = justificatifUrl; }
}
