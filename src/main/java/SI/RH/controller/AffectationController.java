package SI.RH.controller;

import SI.RH.dto.AffectationRequest;
import SI.RH.dto.AffectationResponse;
import SI.RH.service.AffectationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/affectations")
public class AffectationController {

    private final AffectationService affectationService;

    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @PostMapping
    public ResponseEntity<AffectationResponse> creer(@Valid @RequestBody AffectationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(affectationService.creer(req));
    }

    @GetMapping
    public List<AffectationResponse> historique(@RequestParam Integer employeId) {
        return affectationService.historique(employeId);
    }

    @GetMapping("/actuelle")
    public ResponseEntity<AffectationResponse> actuelle(@RequestParam Integer employeId) {
        return affectationService.actuelle(employeId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/{id}/cloturer")
    public AffectationResponse cloturer(@PathVariable Integer id,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return affectationService.cloturer(id, dateFin);
    }

    @GetMapping("/poste/{posteId}/vacance")
    public ResponseEntity<Boolean> vacance(@PathVariable Integer posteId) {
        return ResponseEntity.ok(affectationService.estPosteVacant(posteId));
    }
}