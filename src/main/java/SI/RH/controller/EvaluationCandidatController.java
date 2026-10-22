package SI.RH.controller;

import SI.RH.entity.EvaluationCandidat;
import SI.RH.service.EvaluationCandidatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationCandidatController {
    private final EvaluationCandidatService service;

    public EvaluationCandidatController(EvaluationCandidatService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluationCandidat creer(@RequestBody EvaluationCandidat evaluation) {
        return service.creer(evaluation);
    }

    @GetMapping
    public List<EvaluationCandidat> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public EvaluationCandidat trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
