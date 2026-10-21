package SI.RH.controller;

import SI.RH.dto.CreerOffreEmploiRequest;
import SI.RH.entity.OffreEmploi;
import SI.RH.service.OffreEmploiService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public OffreEmploi creerOffre(
            @Valid @RequestBody CreerOffreEmploiRequest request) {

        return offreService.creerOffre(
                request.offre(),
                request.action());
    }
}