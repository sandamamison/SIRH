package SI.RH.controller;

import SI.RH.dto.*;
import SI.RH.entity.enums.StatutDemande;
import SI.RH.service.DemandeRecrutementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
public class DemandeRecrutementController {

    private final DemandeRecrutementService service;

    public DemandeRecrutementController(DemandeRecrutementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DemandeResponse creer(@Valid @RequestBody DemandeRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    public DemandeResponse modifier(@PathVariable Integer id, @Valid @RequestBody DemandeRequest req) {
        return service.modifier(id, req);
    }

    @GetMapping
    public List<DemandeResponse> lister(
        @RequestParam(required = false) StatutDemande statut,
        @RequestParam(required = false) Integer demandeurId
    ) {
        return service.lister(statut, demandeurId);
    }

    @GetMapping("/{id}")
    public DemandeDetailResponse detail(@PathVariable Integer id) {
        return service.detail(id);
    }

    @PostMapping("/{id}/soumettre")
    public DemandeResponse soumettre(@PathVariable Integer id, @Valid @RequestBody ActionDemandeRequest a) {
        return service.soumettre(id, a);
    }

    @PostMapping("/{id}/valider")
    public DemandeResponse valider(@PathVariable Integer id, @Valid @RequestBody ActionDemandeRequest a) {
        return service.valider(id, a);
    }

    @PostMapping("/{id}/refuser")
    public DemandeResponse refuser(@PathVariable Integer id, @Valid @RequestBody ActionDemandeRequest a) {
        return service.refuser(id, a);
    }
}
