package SI.RH.service;

import SI.RH.dto.AffectationRequest;
import SI.RH.dto.AffectationResponse;
import SI.RH.entity.Affectation;
import SI.RH.entity.Poste;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.AffectationRepository;
import SI.RH.repository.PosteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AffectationService {

    private final AffectationRepository affectationRepository;
    private final PosteRepository posteRepository;

    public AffectationService(AffectationRepository affectationRepository,
                              PosteRepository posteRepository) {
        this.affectationRepository = affectationRepository;
        this.posteRepository = posteRepository;
    }

    public AffectationResponse creer(AffectationRequest req) {
        // Poste doit exister
        Poste poste = posteRepository.findById(req.posteId())
            .orElseThrow(() -> new NotFoundException("Poste", req.posteId()));

        verifierDates(req.dateDebut(), req.dateFin());

        // RG4-04 : non-chevauchement (garde-fou applicatif)
        // On recharge l'employe et on regarde ses affectations actives sur la periode.
        List<Affectation> existantes =
            affectationRepository.findByEmployeIdOrderByDateDebutDesc(req.employeId());

        for (Affectation a : existantes) {
            if (chevauche(a, req.dateDebut(), req.dateFin())) {
                throw new BusinessException(
                    "L'employe a deja une affectation sur cette periode (RG4-04)");
            }
        }

        Affectation a = new Affectation();
        a.setEmployeId(req.employeId());
        a.setPosteId(req.posteId());
        a.setDateDebut(req.dateDebut());
        a.setDateFin(req.dateFin());

        a = affectationRepository.save(a);
        return versReponse(a, poste);
    }

    public AffectationResponse cloturer(Integer id, LocalDate dateFin) {
        Affectation a = trouver(id);

        if (a.getDateFin() != null) {
            throw new BusinessException("Cette affectation est deja cloturee");
        }
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }
        if (dateFin.isBefore(a.getDateDebut())) {
            throw new BusinessException("La date de fin doit etre >= date de debut");
        }

        a.setDateFin(dateFin);
        affectationRepository.save(a);

        Poste poste = posteRepository.findById(a.getPosteId()).orElse(null);
        return versReponse(a, poste);
    }

    @Transactional(readOnly = true)
    public List<AffectationResponse> historique(Integer employeId) {
        return affectationRepository.findByEmployeIdOrderByDateDebutDesc(employeId).stream()
            .map(a -> versReponse(a, posteRepository.findById(a.getPosteId()).orElse(null)))
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<AffectationResponse> actuelle(Integer employeId) {
        return affectationRepository.findFirstByEmployeIdAndDateFinIsNull(employeId)
            .map(a -> versReponse(a, posteRepository.findById(a.getPosteId()).orElse(null)));
    }

    @Transactional(readOnly = true)
    public boolean estPosteVacant(Integer posteId) {
        return !affectationRepository.existsByPosteIdAndDateFinIsNull(posteId);
    }

    public Affectation trouver(Integer id) {
        return affectationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Affectation", id));
    }

    // --- Helpers ---

    private void verifierDates(LocalDate debut, LocalDate fin) {
        if (debut == null) {
            throw new BusinessException("La date de debut est obligatoire");
        }
        if (fin != null && fin.isBefore(debut)) {
            throw new BusinessException("La date de fin doit etre >= date de debut");
        }
    }

    private boolean chevauche(Affectation a, LocalDate debut, LocalDate fin) {
        LocalDate finA = a.getDateFin() != null ? a.getDateFin() : LocalDate.MAX;
        LocalDate finN = fin != null ? fin : LocalDate.MAX;
        return !debut.isAfter(finA) && !a.getDateDebut().isAfter(finN);
    }

    private AffectationResponse versReponse(Affectation a, Poste poste) {
        String titre = poste != null ? poste.getTitre() : "Inconnu";
        return new AffectationResponse(
            a.getId(),
            a.getEmployeId(),
            a.getPosteId(),
            titre,
            a.getDateDebut(),
            a.getDateFin(),
            a.getDateFin() == null
        );
    }
}