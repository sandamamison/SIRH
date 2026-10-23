package SI.RH.controller;

import SI.RH.dto.ChangementStatutContratRequest;
import SI.RH.dto.ContratRequest;
import SI.RH.dto.ContratResponse;
import SI.RH.service.ContratService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
public class ContratController {

    private final ContratService contratService;

    public ContratController(ContratService contratService) {
        this.contratService = contratService;
    }

    @PostMapping
    public ResponseEntity<ContratResponse> creer(@Valid @RequestBody ContratRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.creer(req));
    }

    @GetMapping
    public List<ContratResponse> historique(@RequestParam Integer employeId) {
        return contratService.historique(employeId);
    }

    @GetMapping("/{id}")
    public ContratResponse detail(@PathVariable Integer id) {
        return contratService.versReponse(contratService.trouver(id));
    }

    @GetMapping("/actif")
    public ResponseEntity<ContratResponse> actif(@RequestParam Integer employeId) {
        return contratService.contratActif(employeId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/{id}/signer")
    public ContratResponse signer(@PathVariable Integer id,
                                  @RequestParam Integer employeSignataireId) {
        return contratService.signer(id, employeSignataireId);
    }

    @PutMapping("/{id}/statut")
    public ContratResponse changerStatut(@PathVariable Integer id,
                                         @Valid @RequestBody ChangementStatutContratRequest req) {
        return contratService.changerStatut(id, req);
    }

    @PostMapping("/{id}/renouveler")
    public ResponseEntity<ContratResponse> renouveler(@PathVariable Integer id,
                                                      @Valid @RequestBody ContratRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(contratService.renouveler(id, req));
    }
}