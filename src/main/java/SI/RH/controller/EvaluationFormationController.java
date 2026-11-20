package SI.RH.controller;

import SI.RH.entity.Competence;
import SI.RH.entity.EmployeCompetence;
import SI.RH.entity.EvaluationRh;
import SI.RH.entity.Formation;
import SI.RH.entity.ObjectifEvaluation;
import SI.RH.repository.CompetenceRepository;
import SI.RH.repository.EmployeCompetenceRepository;
import SI.RH.repository.EvaluationRhRepository;
import SI.RH.repository.FormationRepository;
import SI.RH.repository.ObjectifEvaluationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EvaluationFormationController {
    private final CompetenceRepository competences;
    private final EmployeCompetenceRepository employeCompetences;
    private final ObjectifEvaluationRepository objectifs;
    private final EvaluationRhRepository evaluations;
    private final FormationRepository formations;

    public EvaluationFormationController(CompetenceRepository competences,
                                         EmployeCompetenceRepository employeCompetences,
                                         ObjectifEvaluationRepository objectifs,
                                         EvaluationRhRepository evaluations,
                                         FormationRepository formations) {
        this.competences = competences;
        this.employeCompetences = employeCompetences;
        this.objectifs = objectifs;
        this.evaluations = evaluations;
        this.formations = formations;
    }

    @GetMapping("/competences")
    public List<Competence> listCompetences() { return competences.findAll(); }

    @PostMapping("/competences")
    public ResponseEntity<Competence> createCompetence(@RequestBody Competence value) {
        return created(competences.save(value));
    }

    @GetMapping("/employe-competences")
    public List<EmployeCompetence> listEmployeCompetences() { return employeCompetences.findAll(); }

    @PostMapping("/employe-competences")
    public ResponseEntity<EmployeCompetence> createEmployeCompetence(@RequestBody EmployeCompetence value) {
        validateLevel(value.getNiveauAcquis(), "niveauAcquis");
        validateLevel(value.getNiveauRequis(), "niveauRequis");
        return created(employeCompetences.save(value));
    }

    @GetMapping("/objectifs-evaluation")
    public List<ObjectifEvaluation> listObjectifs() { return objectifs.findAll(); }

    @PostMapping("/objectifs-evaluation")
    public ResponseEntity<ObjectifEvaluation> createObjectif(@RequestBody ObjectifEvaluation value) {
        return created(objectifs.save(value));
    }

    @GetMapping("/evaluations-rh")
    public List<EvaluationRh> listEvaluations() { return evaluations.findAll(); }

    @PostMapping("/evaluations-rh")
    public ResponseEntity<EvaluationRh> createEvaluation(@RequestBody EvaluationRh value) {
        return created(evaluations.save(value));
    }

    @GetMapping("/formations")
    public List<Formation> listFormations() { return formations.findAll(); }

    @PostMapping("/formations")
    public ResponseEntity<Formation> createFormation(@RequestBody Formation value) {
        return created(formations.save(value));
    }

    private <T> ResponseEntity<T> created(T value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(value);
    }

    private void validateLevel(Integer level, String field) {
        if (level == null || level < 1 || level > 5) {
            throw new IllegalArgumentException(field + " doit être compris entre 1 et 5");
        }
    }
}
