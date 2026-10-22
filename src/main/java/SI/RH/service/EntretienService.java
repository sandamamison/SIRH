package SI.RH.service;

import SI.RH.entity.Entretien;
import SI.RH.repository.EntretienRepository;
import SI.RH.repository.CandidatureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class EntretienService {

    private final EntretienRepository repository;
    private final CandidatureRepository candidatureRepository;

    public EntretienService(
            EntretienRepository repository,
            CandidatureRepository candidatureRepository) {
        this.repository = repository;
        this.candidatureRepository = candidatureRepository;
    }

    public Entretien creer(Entretien entretien) {
        if (entretien.getCandidatureId() == null
                || entretien.getDateHeure() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La candidature et la date sont obligatoires");
        }

        if (!candidatureRepository.existsById(
                entretien.getCandidatureId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Candidature introuvable");
        }

        entretien.setId(null);
        return repository.save(entretien);
    }

    @Transactional(readOnly = true)
    public List<Entretien> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Entretien> listerParCandidature(Long candidatureId) {
        if (!candidatureRepository.existsById(candidatureId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Candidature introuvable");
        }

        return repository.findByCandidatureId(candidatureId);
    }

    @Transactional(readOnly = true)
    public Entretien trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Entretien introuvable"));
    }

    public Entretien modifierStatut(
            Long id,
            SI.RH.entity.enums.StatutEntretien statut) {
        if (statut == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Statut obligatoire");
        }

        Entretien entretien = trouver(id);
        entretien.setStatut(statut);

        return repository.save(entretien);
    }

    public void supprimer(Long id) {
        repository.delete(trouver(id));
    }
}