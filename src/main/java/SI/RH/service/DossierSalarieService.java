package SI.RH.service;

import SI.RH.dto.AffectationResponse;
import SI.RH.dto.ContratResponse;
import SI.RH.dto.DossierSalarieResponse;
import SI.RH.entity.Document;
import SI.RH.entity.Employe;
import SI.RH.entity.Onboarding;
import SI.RH.entity.Personne;
import SI.RH.entity.enums.TypeAudit;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.EmployeRepository;
import SI.RH.repository.PersonneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DossierSalarieService {

    private final EmployeRepository employeRepository;
    private final PersonneRepository personneRepository;
    private final ContratService contratService;
    private final AffectationService affectationService;
    private final DocumentService documentService;
    private final OnboardingService onboardingService;
    private final JournalAuditService journalAuditService;

    public DossierSalarieService(EmployeRepository employeRepository,
                                 PersonneRepository personneRepository,
                                 ContratService contratService,
                                 AffectationService affectationService,
                                 DocumentService documentService,
                                 OnboardingService onboardingService,
                                 JournalAuditService journalAuditService) {
        this.employeRepository = employeRepository;
        this.personneRepository = personneRepository;
        this.contratService = contratService;
        this.affectationService = affectationService;
        this.documentService = documentService;
        this.onboardingService = onboardingService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * RG5-04 : Consultation du dossier salarié centralisé avec journalisation d'audit automatique.
     */
    public DossierSalarieResponse consulterDossierComplet(Long employeId, Long acteurId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new NotFoundException("Employe", employeId));

        Personne personne = personneRepository.findById(employe.getPersonneId())
                .orElseThrow(() -> new NotFoundException("Personne", employe.getPersonneId()));

        List<ContratResponse> contrats = contratService.historique(employeId.intValue());
        List<AffectationResponse> affectations = affectationService.historique(employeId.intValue());
        List<Document> documents = documentService.getDocumentsParEmploye(employeId);
        List<Onboarding> onboardings = onboardingService.getParEmploye(employeId);

        if (acteurId != null) {
            journalAuditService.enregistrer(
                    acteurId,
                    TypeAudit.CONSULTATION,
                    "employes",
                    employeId,
                    "Consultation du dossier administratif complet du salarié " + employe.getMatricule()
            );
        }

        return new DossierSalarieResponse(employe, personne, contrats, affectations, documents, onboardings);
    }
}
