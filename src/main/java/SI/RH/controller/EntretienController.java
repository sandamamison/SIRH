package SI.RH.controller;

import SI.RH.entity.Entretien;
import SI.RH.entity.enums.StatutEntretien;
import SI.RH.service.EntretienService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entretiens")
public class EntretienController {

    private final EntretienService service;

    public EntretienController(EntretienService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entretien creer(@RequestBody Entretien entretien) {
        return service.creer(entretien);
    }

    @GetMapping
    public List<Entretien> lister(
            @RequestParam(required = false) Long candidatureId) {
        if (candidatureId != null) {
            return service.listerParCandidature(candidatureId);
        }

        return service.lister();
    }

    @GetMapping("/{id}")
    public Entretien trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PatchMapping("/{id}/statut")
    public Entretien modifierStatut(
            @PathVariable Long id,
            @RequestParam StatutEntretien statut) {
        return service.modifierStatut(id, statut);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}