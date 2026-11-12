package SI.RH.service;

import SI.RH.entity.Horaire;
import SI.RH.entity.Planning;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.HoraireRepository;
import SI.RH.repository.PlanningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final HoraireRepository horaireRepository;

    public PlanningService(PlanningRepository planningRepository, HoraireRepository horaireRepository) {
        this.planningRepository = planningRepository;
        this.horaireRepository = horaireRepository;
    }

    public Planning assignerHoraire(Long employeId, Long horaireId, LocalDate dateSemaine, Long regimeId) {
        LocalDate lundi = dateSemaine.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Horaire horaire = horaireRepository.findById(horaireId)
                .orElseThrow(() -> new NotFoundException("Horaire", horaireId));

        Planning planning = planningRepository.findByEmployeIdAndSemaineDebut(employeId, lundi)
                .orElse(new Planning());

        planning.setEmployeId(employeId);
        planning.setHoraireId(horaire.getId());
        planning.setSemaineDebut(lundi);
        planning.setRegimeTravailId(regimeId);

        return planningRepository.save(planning);
    }

    @Transactional(readOnly = true)
    public List<Planning> getPlanningsParEmploye(Long employeId) {
        return planningRepository.findByEmployeId(employeId);
    }

    @Transactional(readOnly = true)
    public Optional<Planning> getPlanningSemaine(Long employeId, LocalDate date) {
        LocalDate lundi = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return planningRepository.findByEmployeIdAndSemaineDebut(employeId, lundi);
    }
}
