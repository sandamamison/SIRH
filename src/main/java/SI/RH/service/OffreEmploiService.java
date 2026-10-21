package SI.RH.service;

import SI.RH.entity.DemandeRecrutement;
import SI.RH.entity.OffreEmploi;
import SI.RH.entity.enums.StatutDemande;
import SI.RH.repository.DemandeRecrutementRepository;
import SI.RH.repository.OffreEmploiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import SI.RH.dto.ActionDemandeRequest;

import java.util.List;

@Service
@Transactional
public class OffreEmploiService {

    private final OffreEmploiRepository offreRepository;
    private final DemandeRecrutementRepository demandeRepository;
    private final DemandeRecrutementService demandeRecrutementService;

    public OffreEmploiService(
        OffreEmploiRepository offreRepository,
        DemandeRecrutementRepository demandeRepository,
        DemandeRecrutementService demandeRecrutementService) {
    this.offreRepository = offreRepository;
    this.demandeRepository = demandeRepository;
    this.demandeRecrutementService = demandeRecrutementService;
}

    @Transactional(readOnly = true)
    public List<OffreEmploi> listerOffres() {
        return offreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public OffreEmploi trouverParId(Integer id) {
        return offreRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Offre d'emploi introuvable : " + id));
    }

    public OffreEmploi creerOffre(
        OffreEmploi offre,
        ActionDemandeRequest action) {

    Integer demandeId = offre.getDemandeRecrutementId();

    if (demandeId == null) {
        throw new IllegalArgumentException(
                "L'identifiant de la demande de recrutement est obligatoire.");
    }

    if (action == null || action.employeActeurId() == null) {
        throw new IllegalArgumentException(
                "L'identifiant de l'employé acteur est obligatoire.");
    }

    DemandeRecrutement demande = demandeRepository.findById(demandeId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Demande de recrutement introuvable : " + demandeId));

    if (demande.getStatut() != StatutDemande.VALIDEE_DIRECTION) {
        throw new IllegalArgumentException(
                "La demande doit être validée par la direction avant la création de l'offre.");
    }

    if (offreRepository.existsByDemandeRecrutementId(demandeId)) {
        throw new IllegalArgumentException(
                "Une offre existe déjà pour cette demande.");
    }

    demandeRecrutementService.marquerRecrutementOuvert(
            demandeId,
            action.employeActeurId());

    offre.setId(null);
    return offreRepository.save(offre);
}
}
