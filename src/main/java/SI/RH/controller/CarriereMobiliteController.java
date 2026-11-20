package SI.RH.controller;

import SI.RH.entity.HistoriqueAffectationCarriere;
import SI.RH.entity.HistoriqueSalaire;
import SI.RH.entity.Mutation;
import SI.RH.entity.Promotion;
import SI.RH.repository.HistoriqueAffectationCarriereRepository;
import SI.RH.repository.HistoriqueSalaireRepository;
import SI.RH.repository.MutationRepository;
import SI.RH.repository.PromotionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CarriereMobiliteController {
    private final HistoriqueAffectationCarriereRepository carrieres;
    private final PromotionRepository promotions;
    private final MutationRepository mutations;
    private final HistoriqueSalaireRepository salaires;

    public CarriereMobiliteController(HistoriqueAffectationCarriereRepository carrieres,
                                      PromotionRepository promotions,
                                      MutationRepository mutations,
                                      HistoriqueSalaireRepository salaires) {
        this.carrieres = carrieres;
        this.promotions = promotions;
        this.mutations = mutations;
        this.salaires = salaires;
    }

    @GetMapping("/carrieres")
    public List<HistoriqueAffectationCarriere> listCarrieres() { return carrieres.findAll(); }

    @PostMapping("/carrieres")
    public ResponseEntity<HistoriqueAffectationCarriere> createCarriere(@RequestBody HistoriqueAffectationCarriere value) {
        return created(carrieres.save(value));
    }

    @GetMapping("/promotions")
    public List<Promotion> listPromotions() { return promotions.findAll(); }

    @PostMapping("/promotions")
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion value) {
        return created(promotions.save(value));
    }

    @GetMapping("/mutations")
    public List<Mutation> listMutations() { return mutations.findAll(); }

    @PostMapping("/mutations")
    public ResponseEntity<Mutation> createMutation(@RequestBody Mutation value) {
        return created(mutations.save(value));
    }

    @GetMapping("/historiques-salaires")
    public List<HistoriqueSalaire> listSalaires() { return salaires.findAll(); }

    @PostMapping("/historiques-salaires")
    public ResponseEntity<HistoriqueSalaire> createSalaire(@RequestBody HistoriqueSalaire value) {
        return created(salaires.save(value));
    }

    private <T> ResponseEntity<T> created(T value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(value);
    }
}
