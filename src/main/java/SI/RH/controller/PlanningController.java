package SI.RH.controller;

import SI.RH.entity.Planning;
import SI.RH.service.PlanningService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/plannings")
public class PlanningController {

    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    public record PlanningAssignationRequest(
            Long employeId,
            Long horaireId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSemaine,
            Long regimeId
    ) {}

    @PostMapping
    public ResponseEntity<Planning> assigner(@RequestBody PlanningAssignationRequest req) {
        Planning p = planningService.assignerHoraire(
                req.employeId(),
                req.horaireId(),
                req.dateSemaine() != null ? req.dateSemaine() : LocalDate.now(),
                req.regimeId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @GetMapping("/employe/{employeId}")
    public List<Planning> getPlannings(@PathVariable Long employeId) {
        return planningService.getPlanningsParEmploye(employeId);
    }

    @GetMapping("/employe/{employeId}/semaine")
    public ResponseEntity<Planning> getPlanningSemaine(
            @PathVariable Long employeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return planningService.getPlanningSemaine(employeId, d)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
