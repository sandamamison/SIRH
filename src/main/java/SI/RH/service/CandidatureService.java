package SI.RH.service;

import SI.RH.entity.Candidature;
import SI.RH.entity.enums.StatutCandidature;
import SI.RH.repository.CandidatureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CandidatureService {

    private final CandidatureRepository repository;

    public CandidatureService(CandidatureRepository repository) {
        this.repository = repository;
    }

    public Candidature creer(Candidature candidature) {
        if (candidature.getOffreEmploiId() == null
                || candidature.getPersonneId() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "L'offre et la personne sont obligatoires."
            );
        }

        if (candidature.getPretentionSalariale() != null
                && candidature.getPretentionSalariale() <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La prétention salariale doit être positive."
            );
        }

        if (repository.existsByOffreEmploiIdAndPersonneId(
                candidature.getOffreEmploiId(),
                candidature.getPersonneId())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cette personne a déjà candidaté à cette offre."
            );
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
                "Candidature introuvable : " + id
            ));
    }

    public Candidature changerStatut(Long id, StatutCandidature statut) {
        if (statut == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Le statut est obligatoire."
            );
        }

        Candidature candidature = trouverParId(id);
        candidature.setStatut(statut);

        return repository.save(candidature);
    }

    public void supprimer(Long id) {
        Candidature candidature = trouverParId(id);
        repository.delete(candidature);
    }
}
