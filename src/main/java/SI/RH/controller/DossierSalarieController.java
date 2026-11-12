package SI.RH.controller;

import SI.RH.dto.DossierSalarieResponse;
import SI.RH.service.DossierSalarieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dossier-salarie")
public class DossierSalarieController {

    private final DossierSalarieService dossierSalarieService;

    public DossierSalarieController(DossierSalarieService dossierSalarieService) {
        this.dossierSalarieService = dossierSalarieService;
    }

    @GetMapping("/{employeId}")
    public ResponseEntity<DossierSalarieResponse> consulter(
            @PathVariable Long employeId,
            @RequestParam(required = false) Long acteurId) {
        return ResponseEntity.ok(dossierSalarieService.consulterDossierComplet(employeId, acteurId));
    }
}
