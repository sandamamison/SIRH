package SI.RH.service;

import SI.RH.entity.Onboarding;
import SI.RH.entity.OnboardingTache;
import SI.RH.entity.enums.StatutOnboarding;
import SI.RH.entity.enums.StatutTache;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.OnboardingRepository;
import SI.RH.repository.OnboardingTacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final OnboardingTacheRepository onboardingTacheRepository;

    public OnboardingService(OnboardingRepository onboardingRepository,
                             OnboardingTacheRepository onboardingTacheRepository) {
        this.onboardingRepository = onboardingRepository;
        this.onboardingTacheRepository = onboardingTacheRepository;
    }

    /**
     * RG4-05 : Création automatique d'un parcours d'onboarding à l'embauche / signature de contrat
     * avec la checklist type définie par le cours.
     */
    public Onboarding creerOnboardingAutomatique(Long employeId) {
        Onboarding onboarding = new Onboarding();
        onboarding.setEmployeId(employeId);
        onboarding.setDateDebut(LocalDate.now());
        onboarding.setStatut(StatutOnboarding.EN_COURS);
        onboarding = onboardingRepository.save(onboarding);

        creerTachesChecklistType(onboarding.getId());
        return onboarding;
    }

    private void creerTachesChecklistType(Long onboardingId) {
        String[] libelles = {
            "Contrat signé",
            "Badge d'accès",
            "Compte informatique et messagerie",
            "Matériel (poste, téléphone)",
            "Formation d'intégration",
            "Inscription à la paie"
        };

        for (String libelle : libelles) {
            OnboardingTache tache = new OnboardingTache();
            tache.setOnboardingId(onboardingId);
            tache.setLibelle(libelle);
            tache.setStatut(StatutTache.A_FAIRE);
            tache.setObligatoire(true);
            onboardingTacheRepository.save(tache);
        }
    }

    public OnboardingTache validerTache(Long tacheId) {
        OnboardingTache tache = onboardingTacheRepository.findById(tacheId)
                .orElseThrow(() -> new NotFoundException("OnboardingTache", tacheId));

        tache.setStatut(StatutTache.FAIT);
        onboardingTacheRepository.save(tache);

        // RG4-07 : L'onboarding est terminé quand toutes les tâches obligatoires sont faites
        verifierEtCloturerOnboarding(tache.getOnboardingId());

        return tache;
    }

    public void verifierEtCloturerOnboarding(Long onboardingId) {
        long nonFaites = onboardingTacheRepository.countByOnboardingIdAndObligatoireTrueAndStatutNot(
                onboardingId, StatutTache.FAIT);

        if (nonFaites == 0) {
            Onboarding onboarding = onboardingRepository.findById(onboardingId)
                    .orElseThrow(() -> new NotFoundException("Onboarding", onboardingId));
            onboarding.setStatut(StatutOnboarding.TERMINE);
            onboardingRepository.save(onboarding);
        }
    }

    @Transactional(readOnly = true)
    public List<Onboarding> getParEmploye(Long employeId) {
        return onboardingRepository.findByEmployeId(employeId);
    }

    @Transactional(readOnly = true)
    public List<OnboardingTache> getTaches(Long onboardingId) {
        return onboardingTacheRepository.findByOnboardingId(onboardingId);
    }

    @Transactional(readOnly = true)
    public Onboarding trouver(Long id) {
        return onboardingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Onboarding", id));
    }
}
