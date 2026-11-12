package SI.RH.controller;

import SI.RH.entity.JournalAudit;
import SI.RH.service.JournalAuditService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class JournalAuditController {

    private final JournalAuditService journalAuditService;

    public JournalAuditController(JournalAuditService journalAuditService) {
        this.journalAuditService = journalAuditService;
    }

    @GetMapping("/acteur/{acteurId}")
    public List<JournalAudit> getParActeur(@PathVariable Long acteurId) {
        return journalAuditService.getHistoriqueParActeur(acteurId);
    }

    @GetMapping("/enregistrement")
    public List<JournalAudit> getParEnregistrement(@RequestParam String tableCible,
                                                  @RequestParam Long enregistrementId) {
        return journalAuditService.getHistoriqueParEnregistrement(tableCible, enregistrementId);
    }

    @GetMapping("/periode")
    public List<JournalAudit> getParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return journalAuditService.getAuditEntre(debut, fin);
    }
}
