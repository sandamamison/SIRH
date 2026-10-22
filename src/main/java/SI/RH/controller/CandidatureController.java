package SI.RH.controller;

import SI.RH.entity.Candidature;
import SI.RH.entity.enums.StatutCandidature;
import SI.RH.service.CandidatureService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    private final CandidatureService service;

    public CandidatureController(CandidatureService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Candidature creer(@RequestBody Candidature candidature) {
        return service.creer(candidature);
    }

    @GetMapping
    public List<Candidature> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public Candidature trouverParId(@PathVariable Long id) {
        return service.trouverParId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }

    @PutMapping("/{id}/statut")
public Candidature changerStatut(
        @PathVariable Long id,
        @RequestParam StatutCandidature statut,
        @RequestParam Long employeActeurId,
        @RequestParam(required = false) String commentaire) {

    return service.changerStatut(
            id, statut, employeActeurId, commentaire);
}
}
