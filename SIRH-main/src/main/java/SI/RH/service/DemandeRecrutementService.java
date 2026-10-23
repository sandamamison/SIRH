package SI.RH.service;

import SI.RH.dto.*;
import SI.RH.entity.*;
import SI.RH.entity.enums.*;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DemandeRecrutementService {

    private final DemandeRecrutementRepository demandeRepository;
    private final PosteRepository posteRepository;
    private final DepartementRepository departementRepository;
    private final ValidationDemandeRepository validationRepository;
    private final HistoriqueStatutDemandeRepository historiqueRepository;

    public DemandeRecrutementService(
        DemandeRecrutementRepository demandeRepository,
        PosteRepository posteRepository,
        DepartementRepository departementRepository,
        ValidationDemandeRepository validationRepository,
        HistoriqueStatutDemandeRepository historiqueRepository
    ) {
        this.demandeRepository = demandeRepository;
        this.posteRepository = posteRepository;
        this.departementRepository = departementRepository;
        this.validationRepository = validationRepository;
        this.historiqueRepository = historiqueRepository;
    }

    public DemandeResponse creer(DemandeRequest req) {
        Poste poste = posteRepository.findById(req.posteId())
            .orElseThrow(() -> new NotFoundException("Poste", req.posteId()));

        verifierMotif(req.motif(), req.employeRemplaceId()); // RG1-03

        DemandeRecrutement d = new DemandeRecrutement();
        appliquer(d, req);
        d.setStatut(StatutDemande.BROUILLON);

        d = demandeRepository.save(d);

        // RG1-06 : Traçabilité de la création initiale
        tracer(d, null, StatutDemande.BROUILLON, req.employeDemandeurId(), "Creation de la demande");

        return versReponse(d, poste);
    }

    public DemandeResponse modifier(Integer id, DemandeRequest req) {
        DemandeRecrutement d = trouver(id);

        // RG1-07 : Modifiable uniquement en BROUILLON
        if (!d.getStatut().estModifiable()) {
            throw new BusinessException("Une demande au statut " + d.getStatut() + " n'est plus modifiable (RG1-07)");
        }

        exigerDemandeur(d, req.employeDemandeurId());
        verifierMotif(req.motif(), req.employeRemplaceId());

        Poste poste = posteRepository.findById(req.posteId())
            .orElseThrow(() -> new NotFoundException("Poste", req.posteId()));

        appliquer(d, req);
        d = demandeRepository.save(d);

        return versReponse(d, poste);
    }

    public DemandeResponse soumettre(Integer id, ActionDemandeRequest a) {
        DemandeRecrutement d = trouver(id);
        exigerDemandeur(d, a.employeActeurId());

        changerStatut(d, StatutDemande.SOUMISE, a.employeActeurId(),
            a.commentaire() != null ? a.commentaire() : "Soumission de la demande par le manager");

        return versReponse(d);
    }

    public DemandeResponse valider(Integer id, ActionDemandeRequest a) {
        DemandeRecrutement d = trouver(id);

        // RG1-05 : Niveau déduit du statut courant
        NiveauValidation niveau = NiveauValidation.pourStatut(d.getStatut())
            .orElseThrow(() -> new BusinessException("Aucune validation attendue au statut " + d.getStatut()));

        // RG1-06 : Enregistrement de la décision
        enregistrerValidation(d, niveau, a, DecisionValidation.APPROUVE);

        // Transition d'état
        changerStatut(d, niveau.statutApresApprobation(), a.employeActeurId(), a.commentaire());

        return versReponse(d);
    }

    public DemandeResponse refuser(Integer id, ActionDemandeRequest a) {
        // D6 : Le commentaire est obligatoire pour justifier un refus
        if (a.commentaire() == null || a.commentaire().isBlank()) {
            throw new BusinessException("Le commentaire est obligatoire pour refuser une demande");
        }

        DemandeRecrutement d = trouver(id);

        NiveauValidation niveau = NiveauValidation.pourStatut(d.getStatut())
            .orElseThrow(() -> new BusinessException("Aucune validation attendue au statut " + d.getStatut()));

        enregistrerValidation(d, niveau, a, DecisionValidation.REJETE);

        changerStatut(d, StatutDemande.REJETEE, a.employeActeurId(), a.commentaire());

        return versReponse(d);
    }

    /**
     * Contrat pour le Bloc 2 (D8) : appelé à la création de l'offre issue d'une demande VALIDEE_DIRECTION.
     */
    public void marquerRecrutementOuvert(Integer id, Integer acteurId) {
        DemandeRecrutement d = trouver(id);
        changerStatut(d, StatutDemande.RECRUTEMENT_OUVERT, acteurId, "Offre d'emploi creee - Recrutement ouvert");
    }

    @Transactional(readOnly = true)
    public List<DemandeResponse> lister(StatutDemande statut, Integer demandeurId) {
        List<DemandeRecrutement> demandes;

        if (statut != null && demandeurId != null) {
            demandes = demandeRepository.findByStatutAndEmployeDemandeurIdOrderByDateCreationDesc(statut, demandeurId);
        } else if (statut != null) {
            demandes = demandeRepository.findByStatutOrderByDateCreationDesc(statut);
        } else if (demandeurId != null) {
            demandes = demandeRepository.findByEmployeDemandeurIdOrderByDateCreationDesc(demandeurId);
        } else {
            demandes = demandeRepository.findAllByOrderByDateCreationDesc();
        }

        return demandes.stream()
            .map(this::versReponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public DemandeDetailResponse detail(Integer id) {
        DemandeRecrutement d = trouver(id);
        DemandeResponse demandeResp = versReponse(d);

        List<ValidationDemandeResponse> validations = validationRepository
            .findByDemandeRecrutementIdOrderByDateDecision(id).stream()
            .map(v -> new ValidationDemandeResponse(
                v.getId(),
                v.getEmployeValideurId(),
                v.getNiveau(),
                v.getDecision(),
                v.getCommentaire(),
                v.getDateDecision()
            ))
            .toList();

        List<HistoriqueStatutDemandeResponse> historique = historiqueRepository
            .findByDemandeRecrutementIdOrderByDateTransition(id).stream()
            .map(h -> new HistoriqueStatutDemandeResponse(
                h.getId(),
                h.getAncienStatut(),
                h.getNouveauStatut(),
                h.getEmployeActeurId(),
                h.getCommentaire(),
                h.getDateTransition()
            ))
            .toList();

        return new DemandeDetailResponse(demandeResp, validations, historique);
    }

    public DemandeRecrutement trouver(Integer id) {
        return demandeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("DemandeRecrutement", id));
    }

    private void verifierMotif(MotifRecrutement motif, Integer employeRemplaceId) {
        if (motif == MotifRecrutement.REMPLACEMENT && employeRemplaceId == null) {
            throw new BusinessException("Un remplacement doit obligatoirement referencer l'employe remplace (RG1-03)");
        }
        if (motif == MotifRecrutement.CREATION && employeRemplaceId != null) {
            throw new BusinessException("Une creation de poste ne doit pas specifier d'employe remplace (RG1-03)");
        }
    }

    private void exigerDemandeur(DemandeRecrutement d, Integer acteurId) {
        if (!d.getEmployeDemandeurId().equals(acteurId)) {
            throw new BusinessException("Seul le demandeur original (employe " + d.getEmployeDemandeurId() +
                ") peut effectuer cette action sur la demande");
        }
    }

    private void changerStatut(DemandeRecrutement d, StatutDemande cible, Integer acteurId, String commentaire) {
        StatutDemande ancien = d.getStatut();
        if (!ancien.peutPasserA(cible)) {
            throw new BusinessException("Transition interdite : " + ancien + " -> " + cible);
        }
        d.setStatut(cible);
        demandeRepository.save(d);
        tracer(d, ancien, cible, acteurId, commentaire);
    }

    private void tracer(DemandeRecrutement d, StatutDemande ancien, StatutDemande nouveau, Integer acteurId, String commentaire) {
        HistoriqueStatutDemande h = new HistoriqueStatutDemande();
        h.setDemandeRecrutementId(d.getId());
        h.setAncienStatut(ancien);
        h.setNouveauStatut(nouveau);
        h.setEmployeActeurId(acteurId);
        h.setCommentaire(commentaire);
        historiqueRepository.save(h);
    }

    private void enregistrerValidation(DemandeRecrutement d, NiveauValidation niveau, ActionDemandeRequest a, DecisionValidation decision) {
        ValidationDemande v = new ValidationDemande();
        v.setDemandeRecrutementId(d.getId());
        v.setEmployeValideurId(a.employeActeurId());
        v.setNiveau(niveau);
        v.setDecision(decision);
        v.setCommentaire(a.commentaire());
        validationRepository.save(v);
    }

    private void appliquer(DemandeRecrutement d, DemandeRequest req) {
        d.setPosteId(req.posteId());
        d.setMotif(req.motif());
        d.setJustification(req.justification());
        d.setEmployeRemplaceId(req.employeRemplaceId());
        d.setEffectifDemande(req.effectifDemande() != null ? req.effectifDemande() : 1);
        d.setBudgetPrevu(req.budgetPrevu());
        d.setDateSouhaitee(req.dateSouhaitee());
        d.setEmployeDemandeurId(req.employeDemandeurId());
    }

    public DemandeResponse versReponse(DemandeRecrutement d) {
        Poste p = posteRepository.findById(d.getPosteId()).orElse(null);
        return versReponse(d, p);
    }

    public DemandeResponse versReponse(DemandeRecrutement d, Poste p) {
        String posteTitre = p != null ? p.getTitre() : "Inconnu";
        Integer departementId = p != null ? p.getDepartementId() : null;
        String departementNom = "Inconnu";

        if (departementId != null) {
            departementNom = departementRepository.findById(departementId)
                .map(Departement::getNomDepartement)
                .orElse("Inconnu");
        }

        return new DemandeResponse(
            d.getId(),
            d.getPosteId(),
            posteTitre,
            departementId,
            departementNom,
            d.getMotif(),
            d.getJustification(),
            d.getEmployeRemplaceId(),
            d.getEffectifDemande(),
            d.getBudgetPrevu(),
            d.getDateSouhaitee(),
            d.getStatut(),
            d.getEmployeDemandeurId(),
            d.getDateCreation()
        );
    }
}
