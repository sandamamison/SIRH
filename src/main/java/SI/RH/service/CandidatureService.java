package SI.RH.service;

import SI.RH.entity.Candidature;
import SI.RH.entity.DemandeRecrutement;
import SI.RH.entity.HistoriqueStatutCandidature;
import SI.RH.entity.OffreEmploi;
import SI.RH.entity.enums.StatutCandidature;
import SI.RH.entity.enums.StatutOffre;
import SI.RH.repository.CandidatureRepository;
import SI.RH.repository.DemandeRecrutementRepository;
import SI.RH.repository.HistoriqueStatutCandidatureRepository;
import SI.RH.repository.OffreEmploiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CandidatureService {

    private final CandidatureRepository repository;
    private final HistoriqueStatutCandidatureRepository historiqueRepository;
    private final OffreEmploiRepository offreRepository;
    private final DemandeRecrutementRepository demandeRepository;

    public CandidatureService(
            CandidatureRepository repository,
            HistoriqueStatutCandidatureRepository historiqueRepository,
            OffreEmploiRepository offreRepository,
            DemandeRecrutementRepository demandeRepository) {
        this.repository = repository;
        this.historiqueRepository = historiqueRepository;
        this.offreRepository = offreRepository;
        this.demandeRepository = demandeRepository;
    }

    public Candidature creer(Candidature candidature) {
        if (candidature.getOffreEmploiId() == null
                || candidature.getPersonneId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'offre et la personne sont obligatoires.");
        }

        if (candidature.getPretentionSalariale() != null
                && candidature.getPretentionSalariale() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La prétention salariale doit être positive.");
        }

        if (repository.existsByOffreEmploiIdAndPersonneId(
                candidature.getOffreEmploiId(),
                candidature.getPersonneId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette personne a déjà candidaté à cette offre.");
        }

        candidature.setId(null);
        candidature.setStatut(StatutCandidature.RECUE);

        return repository.save(candidature);
    }

    @Transactional(readOnly = true)
    public List<Candidature> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Candidature trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidature introuvable : " + id));
    }

    public Candidature changerStatut(
            Long id,
            StatutCandidature nouveauStatut,
            Long employeActeurId,
            String commentaire) {

        if (nouveauStatut == null || employeActeurId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le nouveau statut et l'identifiant de l'employé sont obligatoires.");
        }

        Candidature candidature = trouverParId(id);
        StatutCandidature ancienStatut = candidature.getStatut();

        if (!ancienStatut.peutPasserA(nouveauStatut)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transition interdite : "
                            + ancienStatut + " -> " + nouveauStatut);
        }

        HistoriqueStatutCandidature historique =
                new HistoriqueStatutCandidature();

        historique.setCandidatureId(candidature.getId());
        historique.setAncienStatut(ancienStatut);
        historique.setNouveauStatut(nouveauStatut);
        historique.setEmployeActeurId(employeActeurId);
        historique.setCommentaire(commentaire);

        historiqueRepository.save(historique);

        candidature.setStatut(nouveauStatut);
        Candidature sauvegardee = repository.save(candidature);

        if (nouveauStatut == StatutCandidature.RETENUE) {
            Integer offreId;

            try {
                offreId = Math.toIntExact(
                        sauvegardee.getOffreEmploiId());
            } catch (ArithmeticException e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Identifiant de l'offre invalide.");
            }

            OffreEmploi offre = offreRepository.findById(offreId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Offre introuvable : " + offreId));

            DemandeRecrutement demande = demandeRepository
                    .findById(offre.getDemandeRecrutementId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Demande de recrutement introuvable."));

            long nombreRetenues =
                    repository.countByOffreEmploiIdAndStatut(
                            sauvegardee.getOffreEmploiId(),
                            StatutCandidature.RETENUE);

            if (nombreRetenues >= demande.getEffectifDemande()
                    && offre.getStatut() == StatutOffre.PUBLIEE) {
                offre.setStatut(StatutOffre.CLOTUREE);
                offre.setDateCloture(java.time.LocalDate.now());
                offreRepository.save(offre);
            }
        }

        return sauvegardee;
    }

    public void supprimer(Long id) {
        Candidature candidature = trouverParId(id);
        repository.delete(candidature);
    }
}