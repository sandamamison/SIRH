package SI.RH.controller;

import SI.RH.entity.IntervenantEntretien;
import SI.RH.service.IntervenantEntretienService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/intervenants-entretien")
public class IntervenantEntretienController {
    private final IntervenantEntretienService service;

    public IntervenantEntretienController(IntervenantEntretienService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IntervenantEntretien creer(@RequestBody IntervenantEntretien intervenant) {
        return service.creer(intervenant);
    }

    @GetMapping
    public List<IntervenantEntretien> lister() {
        return service.lister();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
