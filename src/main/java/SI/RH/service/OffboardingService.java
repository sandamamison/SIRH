package SI.RH.service;

import SI.RH.entity.*;
import SI.RH.exception.ResourceNotFoundException;
import SI.RH.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class OffboardingService {
    private final RuptureContratRepository ruptureRepository;
    private final SoldeToutCompteRepository soldeRepository;
    private final ContratRepository contratRepository;

    public OffboardingService(RuptureContratRepository ruptureRepository,
                              SoldeToutCompteRepository soldeRepository,
                              ContratRepository contratRepository) {
        this.ruptureRepository = ruptureRepository;
        this.soldeRepository = soldeRepository;
        this.contratRepository = contratRepository;
    }

    @Transactional
    public SoldeToutCompte calculerSolde(Long ruptureId) {
        RuptureContrat rupture = ruptureRepository.findById(ruptureId)
                .orElseThrow(() -> new ResourceNotFoundException("Rupture non trouvée : " + ruptureId));
        Contrat contrat = contratRepository.findById(rupture.getContratId().intValue())
                .orElseThrow(() -> new ResourceNotFoundException("Contrat non trouvé : " + rupture.getContratId()));

        BigDecimal salaire = BigDecimal.valueOf(contrat.getSalaireBrut());
        BigDecimal journalier = salaire.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal conges = journalier.multiply(zeroIfNull(rupture.getJoursCongesRestants()));
        BigDecimal preavis = Boolean.TRUE.equals(rupture.getPreavisEffectue())
                ? BigDecimal.ZERO
                : journalier.multiply(BigDecimal.valueOf(zeroIfNull(rupture.getJoursPreavis())));
        BigDecimal licenciement = "LICENCIEMENT".equalsIgnoreCase(rupture.getMotif())
                ? salaire.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal brut = salaire.add(conges).add(preavis).add(licenciement);
        BigDecimal retenues = brut.multiply(BigDecimal.valueOf(0.02)).setScale(2, RoundingMode.HALF_UP);

        SoldeToutCompte solde = soldeRepository.findByRuptureId(ruptureId).orElseGet(SoldeToutCompte::new);
        solde.setRuptureId(ruptureId);
        solde.setEmployeId(rupture.getEmployeId());
        solde.setDateCalcul(LocalDate.now());
        solde.setSalaireDernierMois(salaire);
        solde.setIndemniteCongesPayes(conges);
        solde.setIndemnitePreavis(preavis);
        solde.setIndemniteLicenciement(licenciement);
        solde.setTotalBrut(brut);
        solde.setTotalRetenues(retenues);
        solde.setNetSolde(brut.subtract(retenues));
        return soldeRepository.save(solde);
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }
}
