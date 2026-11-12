package SI.RH.controller;

import SI.RH.entity.Absence;
import SI.RH.repository.AbsenceRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/absences")
public class AbsenceController {

    private final AbsenceRepository absenceRepository;

    public AbsenceController(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    @GetMapping("/employe/{employeId}")
    public List<Absence> getAbsencesParEmploye(@PathVariable Long employeId) {
        return absenceRepository.findByEmployeId(employeId);
    }

    @GetMapping("/employe/{employeId}/periode")
    public List<Absence> getAbsencesParPeriode(
            @PathVariable Long employeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return absenceRepository.findByEmployeIdAndDateDebutBetween(employeId, debut, fin);
    }
}
