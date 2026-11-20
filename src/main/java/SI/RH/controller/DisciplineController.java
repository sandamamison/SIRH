package SI.RH.controller;

import SI.RH.entity.IncidentDisciplinaire;
import SI.RH.entity.ProcedureDisciplinaire;
import SI.RH.entity.SanctionDisciplinaire;
import SI.RH.repository.IncidentDisciplinaireRepository;
import SI.RH.repository.ProcedureDisciplinaireRepository;
import SI.RH.repository.SanctionDisciplinaireRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DisciplineController {
    private final IncidentDisciplinaireRepository incidents;
    private final ProcedureDisciplinaireRepository procedures;
    private final SanctionDisciplinaireRepository sanctions;

    public DisciplineController(IncidentDisciplinaireRepository incidents,
                                ProcedureDisciplinaireRepository procedures,
                                SanctionDisciplinaireRepository sanctions) {
        this.incidents = incidents;
        this.procedures = procedures;
        this.sanctions = sanctions;
    }

    @GetMapping("/incidents-disciplinaires")
    public List<IncidentDisciplinaire> listIncidents() { return incidents.findAll(); }

    @PostMapping("/incidents-disciplinaires")
    public ResponseEntity<IncidentDisciplinaire> createIncident(@RequestBody IncidentDisciplinaire value) {
        return created(incidents.save(value));
    }

    @GetMapping("/procedures-disciplinaires")
    public List<ProcedureDisciplinaire> listProcedures() { return procedures.findAll(); }

    @PostMapping("/procedures-disciplinaires")
    public ResponseEntity<ProcedureDisciplinaire> createProcedure(@RequestBody ProcedureDisciplinaire value) {
        return created(procedures.save(value));
    }

    @GetMapping("/sanctions-disciplinaires")
    public List<SanctionDisciplinaire> listSanctions() { return sanctions.findAll(); }

    @PostMapping("/sanctions-disciplinaires")
    public ResponseEntity<SanctionDisciplinaire> createSanction(@RequestBody SanctionDisciplinaire value) {
        return created(sanctions.save(value));
    }

    private <T> ResponseEntity<T> created(T value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(value);
    }
}
