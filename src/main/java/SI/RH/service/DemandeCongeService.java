package SI.RH.service;

import SI.RH.entity.Absence;
import SI.RH.entity.DemandeConge;
import SI.RH.entity.TypeConge;
import SI.RH.entity.enums.StatutDemandeConge;
import SI.RH.entity.enums.TypeAbsence;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.AbsenceRepository;
import SI.RH.repository.DemandeCongeRepository;
import SI.RH.repository.TypeCongeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class DemandeCongeService {

    private final DemandeCongeRepository demandeCongeRepository;
    private final TypeCongeRepository typeCongeRepository;
    private final SoldeCongeService soldeCongeService;
    private final AbsenceRepository absenceRepository;

    public DemandeCongeService(DemandeCongeRepository demandeCongeRepository,
                               TypeCongeRepository typeCongeRepository,
                               SoldeCongeService soldeCongeService,
                               AbsenceRepository absenceRepository) {
        this.demandeCongeRepository = demandeCongeRepository;
        this.typeCongeRepository = typeCongeRepository;
        this.soldeCongeService = soldeCongeService;
        this.absenceRepository = absenceRepository;
    }

    /**
     * RG7-02, RG7-03 : Soumission d'une demande de congé avec contrôle de chevauchement et solde.
     */
    public DemandeConge soumettre(Long employeId, Long typeCongeId, LocalDate dateDebut,
                                  LocalDate dateFin, String commentaire) {
        if (dateDebut == null || dateFin == null) {
            throw new BusinessException("Les dates de début et fin sont obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new BusinessException("La date de fin doit être postérieure ou égale à la date de début");
        }

        TypeConge type = typeCongeRepository.findById(typeCongeId)
                .orElseThrow(() -> new NotFoundException("TypeConge", typeCongeId));

        // RG7-03 : non-chevauchement des congés
        if (demandeCongeRepository.existsChevauchement(employeId, dateDebut, dateFin, null)) {
            throw new BusinessException("Une demande de congé active existe déjà sur cette période (RG7-03)");
        }

        // RG7-02 : contrôle de disponibilité du solde
        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        soldeCongeService.verifierDisponibilite(employeId, typeCongeId, (double) jours);

        DemandeConge d = new DemandeConge();
        d.setEmployeId(employeId);
        d.setTypeCongeId(typeCongeId);
        d.setDateDebut(dateDebut);
        d.setDateFin(dateFin);
        d.setCommentaire(commentaire);
        d.setStatut(StatutDemandeConge.SOUMISE);

        return demandeCongeRepository.save(d);
    }

    public DemandeConge validerManager(Long demandeId, Long managerId) {
        DemandeConge d = trouver(demandeId);

        if (!d.getStatut().peutPasserA(StatutDemandeConge.VALIDEE_MANAGER)) {
            throw new BusinessException("Transition de statut interdite depuis : " + d.getStatut());
        }

        d.setStatut(StatutDemandeConge.VALIDEE_MANAGER);
        d.setValidateurManagerId(managerId);
        d.setDateValidationManager(LocalDateTime.now());
        return demandeCongeRepository.save(d);
    }

    /**
     * RG7-05 : Validation finale par les RH : mise à jour du solde et création automatique de l'absence.
     */
    public DemandeConge validerRh(Long demandeId, Long rhId) {
        DemandeConge d = trouver(demandeId);

        if (!d.getStatut().peutPasserA(StatutDemandeConge.VALIDEE_RH)) {
            throw new BusinessException("Transition de statut interdite depuis : " + d.getStatut());
        }

        long jours = ChronoUnit.DAYS.between(d.getDateDebut(), d.getDateFin()) + 1;
        // Débit du solde
        soldeCongeService.debiter(d.getEmployeId(), d.getTypeCongeId(), (double) jours);

        d.setStatut(StatutDemandeConge.VALIDEE_RH);
        d.setValidateurRhId(rhId);
        d.setDateValidationRh(LocalDateTime.now());
        d = demandeCongeRepository.save(d);

        // RG7-05 : Création automatique d'une absence correspondante
        Absence abs = new Absence();
        abs.setEmployeId(d.getEmployeId());
        abs.setTypeAbsence(TypeAbsence.CONGE_ANNUEL);
        abs.setDateDebut(d.getDateDebut());
        abs.setDateFin(d.getDateFin());
        abs.setDemandeCongeId(d.getId());
        abs.setJustifiee(true);
        absenceRepository.save(abs);

        return d;
    }

    public DemandeConge refuser(Long demandeId, Long acteurId, String motif) {
        DemandeConge d = trouver(demandeId);

        if (!d.getStatut().peutPasserA(StatutDemandeConge.REFUSEE)) {
            throw new BusinessException("Transition de statut interdite vers REFUSEE depuis : " + d.getStatut());
        }

        d.setStatut(StatutDemandeConge.REFUSEE);
        d.setCommentaire(d.getCommentaire() != null ? d.getCommentaire() + " | Refus : " + motif : motif);
        return demandeCongeRepository.save(d);
    }

    @Transactional(readOnly = true)
    public List<DemandeConge> getDemandesParEmploye(Long employeId) {
        return demandeCongeRepository.findByEmployeId(employeId);
    }

    @Transactional(readOnly = true)
    public DemandeConge trouver(Long id) {
        return demandeCongeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DemandeConge", id));
    }
}
