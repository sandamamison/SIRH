package SI.RH.service;

import SI.RH.entity.SoldeConge;
import SI.RH.exception.BusinessException;
import SI.RH.repository.SoldeCongeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SoldeCongeService {

    private final SoldeCongeRepository soldeCongeRepository;

    public SoldeCongeService(SoldeCongeRepository soldeCongeRepository) {
        this.soldeCongeRepository = soldeCongeRepository;
    }

    @Transactional(readOnly = true)
    public double getSolde(Long employeId, Long typeCongeId) {
        return soldeCongeRepository.findByEmployeIdAndTypeCongeId(employeId, typeCongeId)
                .map(SoldeConge::getSoldeCourant)
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public List<SoldeConge> getSoldesParEmploye(Long employeId) {
        return soldeCongeRepository.findByEmployeId(employeId);
    }

    public void verifierDisponibilite(Long employeId, Long typeCongeId, double joursDemandes) {
        double solde = getSolde(employeId, typeCongeId);
        if (solde < joursDemandes) {
            throw new BusinessException("Solde de congés insuffisant (requis: " + joursDemandes
                    + ", disponible: " + solde + ") - RG7-02");
        }
    }

    public void debiter(Long employeId, Long typeCongeId, double jours) {
        SoldeConge solde = soldeCongeRepository.findByEmployeIdAndTypeCongeId(employeId, typeCongeId)
                .orElseThrow(() -> new BusinessException("Aucun solde trouvé pour cet employé et ce type de congé"));

        if (solde.getSoldeCourant() < jours) {
            throw new BusinessException("Solde de congés insuffisant");
        }

        solde.setSoldeCourant(solde.getSoldeCourant() - jours);
        soldeCongeRepository.save(solde);
    }

    public void crediter(Long employeId, Long typeCongeId, double jours) {
        SoldeConge solde = soldeCongeRepository.findByEmployeIdAndTypeCongeId(employeId, typeCongeId)
                .orElseGet(() -> {
                    SoldeConge sc = new SoldeConge();
                    sc.setEmployeId(employeId);
                    sc.setTypeCongeId(typeCongeId);
                    sc.setSoldeCourant(0.0);
                    return sc;
                });

        solde.setSoldeCourant(solde.getSoldeCourant() + jours);
        soldeCongeRepository.save(solde);
    }
}
