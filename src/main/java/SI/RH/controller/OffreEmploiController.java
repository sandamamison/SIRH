package SI.RH.controller;

import SI.RH.entity.OffreEmploi;
import SI.RH.service.OffreEmploiService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/offres")
public class OffreEmploiController {

    private final OffreEmploiService offreService;

    public OffreEmploiController(OffreEmploiService offreService) {
        this.offreService = offreService;
    }

    @GetMapping
    public List<OffreEmploi> listerOffres() {
        return offreService.listerOffres();
    }

    @GetMapping("/{id}")
    public OffreEmploi trouverParId(@PathVariable Integer id) {
        return offreService.trouverParId(id);
    }

    @PostMapping
@ResponseStatus(HttpStatus.CREATED)
public OffreEmploi creerOffre(@RequestBody OffreEmploi offre) {
    return offreService.creerOffre(offre);
}
}
