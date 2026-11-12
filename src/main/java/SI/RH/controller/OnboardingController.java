package SI.RH.controller;

import SI.RH.entity.Onboarding;
import SI.RH.entity.OnboardingTache;
import SI.RH.service.OnboardingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onboardings")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("/employe/{employeId}")
    public ResponseEntity<Onboarding> creerAutomatique(@PathVariable Long employeId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(onboardingService.creerOnboardingAutomatique(employeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Onboarding> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(onboardingService.trouver(id));
    }

    @GetMapping("/employe/{employeId}")
    public List<Onboarding> obtenirParEmploye(@PathVariable Long employeId) {
        return onboardingService.getParEmploye(employeId);
    }

    @GetMapping("/{id}/taches")
    public List<OnboardingTache> obtenirTaches(@PathVariable Long id) {
        return onboardingService.getTaches(id);
    }

    @PutMapping("/taches/{tacheId}/valider")
    public ResponseEntity<OnboardingTache> validerTache(@PathVariable Long tacheId) {
        return ResponseEntity.ok(onboardingService.validerTache(tacheId));
    }
}
