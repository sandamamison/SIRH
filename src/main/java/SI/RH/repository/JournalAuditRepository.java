package SI.RH.repository;

import SI.RH.entity.JournalAudit;
import SI.RH.entity.enums.TypeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalAuditRepository extends JpaRepository<JournalAudit, Long> {
    List<JournalAudit> findByActeurId(Long acteurId);
    List<JournalAudit> findByTableCibleAndEnregistrementId(String tableCible, Long enregistrementId);
    List<JournalAudit> findByTypeAction(TypeAudit typeAction);
    List<JournalAudit> findByDateActionBetween(LocalDateTime debut, LocalDateTime fin);
}
