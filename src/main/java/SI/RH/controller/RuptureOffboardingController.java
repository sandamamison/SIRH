package SI.RH.controller;

import SI.RH.entity.RuptureContrat;
import SI.RH.entity.SoldeToutCompte;
import SI.RH.repository.RuptureContratRepository;
import SI.RH.repository.SoldeToutCompteRepository;
import SI.RH.service.OffboardingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RuptureOffboardingController {
    private final RuptureContratRepository ruptures;
    private final SoldeToutCompteRepository soldes;
    private final OffboardingService offboarding;

    public RuptureOffboardingController(RuptureContratRepository ruptures,
                                        SoldeToutCompteRepository soldes,
                                        OffboardingService offboarding) {
        this.ruptures = ruptures;
        this.soldes = soldes;
        this.offboarding = offboarding;
    }

    @GetMapping("/ruptures")
    public List<RuptureContrat> listRuptures() { return ruptures.findAll(); }

    @PostMapping("/ruptures")
    public ResponseEntity<RuptureContrat> createRupture(@RequestBody RuptureContrat value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ruptures.save(value));
    }

    @GetMapping("/ruptures/{id}/solde")
    public SoldeToutCompte calculateSolde(@PathVariable Long id) {
        return offboarding.calculerSolde(id);
    }

    @GetMapping("/soldes-tout-compte")
    public List<SoldeToutCompte> listSoldes() { return soldes.findAll(); }
}
