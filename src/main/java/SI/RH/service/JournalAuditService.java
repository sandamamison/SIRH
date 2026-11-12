package SI.RH.service;

import SI.RH.entity.JournalAudit;
import SI.RH.entity.enums.TypeAudit;
import SI.RH.repository.JournalAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class JournalAuditService {

    private final JournalAuditRepository journalAuditRepository;

    public JournalAuditService(JournalAuditRepository journalAuditRepository) {
        this.journalAuditRepository = journalAuditRepository;
    }

    public JournalAudit enregistrer(Long acteurId, TypeAudit typeAction, String tableCible,
                                    Long enregistrementId, String detail) {
        JournalAudit audit = new JournalAudit();
        audit.setActeurId(acteurId);
        audit.setTypeAction(typeAction);
        audit.setTableCible(tableCible);
        audit.setEnregistrementId(enregistrementId);
        audit.setDetail(detail);
        return journalAuditRepository.save(audit);
    }

    @Transactional(readOnly = true)
    public List<JournalAudit> getHistoriqueParActeur(Long acteurId) {
        return journalAuditRepository.findByActeurId(acteurId);
    }

    @Transactional(readOnly = true)
    public List<JournalAudit> getHistoriqueParEnregistrement(String tableCible, Long enregistrementId) {
        return journalAuditRepository.findByTableCibleAndEnregistrementId(tableCible, enregistrementId);
    }

    @Transactional(readOnly = true)
    public List<JournalAudit> getAuditEntre(LocalDateTime debut, LocalDateTime fin) {
        return journalAuditRepository.findByDateActionBetween(debut, fin);
    }
}
