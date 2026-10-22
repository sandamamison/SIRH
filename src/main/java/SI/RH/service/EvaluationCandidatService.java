package SI.RH.service;

import SI.RH.entity.EvaluationCandidat;
import SI.RH.repository.EvaluationCandidatRepository;
import SI.RH.repository.EntretienRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class EvaluationCandidatService {

    private final EvaluationCandidatRepository repository;
    private final EntretienRepository entretienRepository;

    public EvaluationCandidatService(
            EvaluationCandidatRepository repository,
            EntretienRepository entretienRepository) {
        this.repository = repository;
        this.entretienRepository = entretienRepository;
    }

    public EvaluationCandidat creer(EvaluationCandidat evaluation) {
        if (evaluation.getEntretienId() == null
                || evaluation.getEmployeEvaluateurId() == null
                || evaluation.getAvis() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'entretien, l'évaluateur et l'avis sont obligatoires");
        }

        Integer score = evaluation.getScoreGlobal();

        if (score != null && (score < 0 || score > 20)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le score doit être compris entre 0 et 20");
        }

        if (!entretienRepository.existsById(
                evaluation.getEntretienId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Entretien introuvable");
        }

        if (repository.existsByEntretienIdAndEmployeEvaluateurId(
                evaluation.getEntretienId(),
                evaluation.getEmployeEvaluateurId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet employé a déjà évalué cet entretien");
        }

        evaluation.setId(null);
        return repository.save(evaluation);
    }

    @Transactional(readOnly = true)
    public List<EvaluationCandidat> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EvaluationCandidat> listerParEntretien(
            Long entretienId) {
        if (!entretienRepository.existsById(entretienId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Entretien introuvable");
        }

        return repository.findByEntretienId(entretienId);
    }

    @Transactional(readOnly = true)
    public EvaluationCandidat trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Évaluation introuvable"));
    }

    public void supprimer(Long id) {
        repository.delete(trouver(id));
    }
}