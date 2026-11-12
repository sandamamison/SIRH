package SI.RH.controller;

import SI.RH.entity.DemandeConge;
import SI.RH.entity.SoldeConge;
import SI.RH.service.DemandeCongeService;
import SI.RH.service.SoldeCongeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/conges")
public class DemandeCongeController {

    private final DemandeCongeService demandeCongeService;
    private final SoldeCongeService soldeCongeService;

    public DemandeCongeController(DemandeCongeService demandeCongeService,
                                  SoldeCongeService soldeCongeService) {
        this.demandeCongeService = demandeCongeService;
        this.soldeCongeService = soldeCongeService;
    }

    public record DemandeCongeCreationRequest(
            Long employeId,
            Long typeCongeId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            String commentaire
    ) {}

    public record DecisionRequest(
            Long acteurId,
            String motif
    ) {}

    @PostMapping
    public ResponseEntity<DemandeConge> soumettre(@RequestBody DemandeCongeCreationRequest req) {
        DemandeConge d = demandeCongeService.soumettre(
                req.employeId(),
                req.typeCongeId(),
                req.dateDebut(),
                req.dateFin(),
                req.commentaire()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(d);
    }

    @PutMapping("/{id}/valider-manager")
    public ResponseEntity<DemandeConge> validerManager(@PathVariable Long id, @RequestBody DecisionRequest req) {
        return ResponseEntity.ok(demandeCongeService.validerManager(id, req.acteurId()));
    }

    @PutMapping("/{id}/valider-rh")
    public ResponseEntity<DemandeConge> validerRh(@PathVariable Long id, @RequestBody DecisionRequest req) {
        return ResponseEntity.ok(demandeCongeService.validerRh(id, req.acteurId()));
    }

    @PutMapping("/{id}/refuser")
    public ResponseEntity<DemandeConge> refuser(@PathVariable Long id, @RequestBody DecisionRequest req) {
        return ResponseEntity.ok(demandeCongeService.refuser(id, req.acteurId(), req.motif()));
    }

    @GetMapping("/employe/{employeId}")
    public List<DemandeConge> getDemandesParEmploye(@PathVariable Long employeId) {
        return demandeCongeService.getDemandesParEmploye(employeId);
    }

    @GetMapping("/soldes/employe/{employeId}")
    public List<SoldeConge> getSoldesParEmploye(@PathVariable Long employeId) {
        return soldeCongeService.getSoldesParEmploye(employeId);
    }
}
