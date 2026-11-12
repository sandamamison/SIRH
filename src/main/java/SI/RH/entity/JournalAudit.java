package SI.RH.entity;

import SI.RH.entity.enums.TypeAudit;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long acteurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeAudit typeAction;

    @Column(nullable = false, length = 100)
    private String tableCible;

    private Long enregistrementId;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateAction;

    public JournalAudit() {}

    @PrePersist
    void onCreate() {
        dateAction = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActeurId() {
        return acteurId;
    }

    public void setActeurId(Long acteurId) {
        this.acteurId = acteurId;
    }

    public TypeAudit getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(TypeAudit typeAction) {
        this.typeAction = typeAction;
    }

    public String getTableCible() {
        return tableCible;
    }

    public void setTableCible(String tableCible) {
        this.tableCible = tableCible;
    }

    public Long getEnregistrementId() {
        return enregistrementId;
    }

    public void setEnregistrementId(Long enregistrementId) {
        this.enregistrementId = enregistrementId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
}
