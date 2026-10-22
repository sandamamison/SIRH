package SI.RH.service;

import SI.RH.dto.ChangementStatutContratRequest;
import SI.RH.dto.ContratRequest;
import SI.RH.dto.ContratResponse;
import SI.RH.entity.Contrat;
import SI.RH.entity.enums.StatutContrat;
import SI.RH.entity.enums.TypeContrat;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.ContratRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContratService {

    private final ContratRepository contratRepository;

    public ContratService(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
    }

    public ContratResponse creer(ContratRequest req) {
        // RG4-01 : coherence typeContrat <-> dateFin
        verifierCoherenceTypeDate(req.typeContrat(), req.dateFin());
        verifierDates(req.dateDebut(), req.dateFin());

        // RG4-02 : non-chevauchement (double garde-fou applicatif de la contrainte EXCLUDE)
        if (contratRepository.existsChevauchement(
                req.employeId(), req.dateDebut(), req.dateFin(), null)) {
            throw new BusinessException(
                "Un contrat actif existe deja sur cette periode pour cet employe (RG4-02)");
        }

        Contrat c = new Contrat();
        c.setNumeroContrat(genererNumeroContrat(req.employeId()));
        c.setEmployeId(req.employeId());
        c.setTypeContrat(req.typeContrat());
        c.setDateDebut(req.dateDebut());
        c.setDateFin(req.dateFin());
        c.setSalaireBrut(req.salaireBrut());
        c.setStatut(StatutContrat.CREE);
        c.setEmployeCreateurId(req.employeCreateurId());

        c = contratRepository.save(c);
        return versReponse(c);
    }

    public ContratResponse signer(Integer id, Integer employeSignataireId) {
        Contrat c = trouver(id);

        if (employeSignataireId == null) {
            throw new BusinessException("L'employe signataire est obligatoire pour signer un contrat");
        }

        changerStatut(c, StatutContrat.SIGNE, employeSignataireId, LocalDate.now());
        return versReponse(c);
    }

    /**
     * RG4-03 : renouvellement = creation d'un NOUVEAU contrat lie au precedent.
     * Le contrat precedent passe au statut RENOUVELE.
     */
    public ContratResponse renouveler(Integer ancienContratId, ContratRequest req) {
        Contrat ancien = trouver(ancienContratId);

        // Un contrat deja renouvele ne peut pas l'etre a nouveau
        if (contratRepository.existsByContratPrecedentId(ancienContratId)) {
            throw new BusinessException(
                "Ce contrat a deja ete renouvele (RG4-03)");
        }

        // Seul un contrat SIGNE peut etre renouvele
        if (ancien.getStatut() != StatutContrat.SIGNE) {
            throw new BusinessException(
                "Seul un contrat au statut SIGNE peut etre renouvele (statut courant : "
                + ancien.getStatut() + ")");
        }

        // Le nouveau contrat doit demarrer a la fin (ou apres) de l'ancien
        LocalDate finAncien = ancien.getDateFin();
        if (finAncien != null && req.dateDebut().isBefore(finAncien)) {
            throw new BusinessException(
                "La date de debut du nouveau contrat doit etre >= date de fin du precedent (RG4-03)");
        }

        // Meme employe obligatoire
        if (!ancien.getEmployeId().equals(req.employeId())) {
            throw new BusinessException("Le renouvellement doit concerner le meme employe (RG4-03)");
        }

        // Validations habituelles
        verifierCoherenceTypeDate(req.typeContrat(), req.dateFin());
        verifierDates(req.dateDebut(), req.dateFin());

        // Creer le nouveau contrat
        Contrat nouveau = new Contrat();
        nouveau.setNumeroContrat(genererNumeroContrat(req.employeId()));
        nouveau.setEmployeId(req.employeId());
        nouveau.setTypeContrat(req.typeContrat());
        nouveau.setDateDebut(req.dateDebut());
        nouveau.setDateFin(req.dateFin());
        nouveau.setSalaireBrut(req.salaireBrut());
        nouveau.setStatut(StatutContrat.CREE);
        nouveau.setEmployeCreateurId(req.employeCreateurId());
        nouveau.setContratPrecedentId(ancienContratId);
        nouveau = contratRepository.save(nouveau);

        // L'ancien passe a RENOUVELE
        ancien.setStatut(StatutContrat.RENOUVELE);
        contratRepository.save(ancien);

        return versReponse(nouveau);
    }

    /**
     * Transitions terminales : TERMINE, ROMPU, NON_RENOUVELE.
     * Utilise peutPasserA(...) pour garantir la coherence de la machine a etats.
     */
    public ContratResponse changerStatut(Integer id, ChangementStatutContratRequest req) {
        Contrat c = trouver(id);

        // Cas particulier : passage CREE -> SIGNE
        if (req.statut() == StatutContrat.SIGNE) {
            return signer(id, req.employeActeurId());
        }

        changerStatut(c, req.statut(), req.employeActeurId(), req.dateSignature());
        return versReponse(c);
    }

    @Transactional(readOnly = true)
    public List<ContratResponse> historique(Integer employeId) {
        return contratRepository.findByEmployeIdOrderByDateDebutDesc(employeId).stream()
            .map(this::versReponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ContratResponse> contratActif(Integer employeId) {
        return contratRepository
            .findFirstByEmployeIdAndDateFinIsNullOrderByDateDebutDesc(employeId)
            .map(this::versReponse);
    }

    public Contrat trouver(Integer id) {
        return contratRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Contrat", id));
    }

    // --- Helpers ---

    private void changerStatut(Contrat c, StatutContrat cible, Integer acteurId, LocalDate dateSignature) {
        StatutContrat ancien = c.getStatut();

        if (!ancien.peutPasserA(cible)) {
            throw new BusinessException(
                "Transition interdite : " + ancien + " -> " + cible);
        }

        c.setStatut(cible);

        if (cible == StatutContrat.SIGNE) {
            c.setDateSignature(dateSignature != null ? dateSignature : LocalDate.now());
            c.setEmployeSignataireId(acteurId);
        } else if (cible == StatutContrat.TERMINE && c.getDateFin() == null) {
            // Un CDI termine doit avoir une date de fin
            c.setDateFin(LocalDate.now());
        }

        contratRepository.save(c);
    }

    private void verifierCoherenceTypeDate(TypeContrat type, LocalDate dateFin) {
        if (type.exigeDateFin() && dateFin == null) {
            throw new BusinessException(
                "Un contrat " + type + " doit avoir une date de fin (RG4-01)");
        }
        if (!type.exigeDateFin() && dateFin != null) {
            throw new BusinessException(
                "Un CDI ne doit pas avoir de date de fin (RG4-01)");
        }
    }

    private void verifierDates(LocalDate debut, LocalDate fin) {
        if (debut == null) {
            throw new BusinessException("La date de debut est obligatoire");
        }
        if (fin != null && fin.isBefore(debut)) {
            throw new BusinessException("La date de fin doit etre >= date de debut");
        }
    }

    private String genererNumeroContrat(Integer employeId) {
        String base = "CTR-" + employeId + "-" +
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String numero = base;
        int suffixe = 1;
        while (contratRepository.existsByNumeroContrat(numero)) {
            numero = base + "-" + suffixe++;
        }
        return numero;
    }

    public ContratResponse versReponse(Contrat c) {
        return new ContratResponse(
            c.getId(),
            c.getNumeroContrat(),
            c.getEmployeId(),
            c.getTypeContrat(),
            c.getDateDebut(),
            c.getDateFin(),
            c.getSalaireBrut(),
            c.getStatut(),
            c.getContratPrecedentId(),
            c.getEmployeCreateurId(),
            c.getEmployeSignataireId(),
            c.getDateSignature(),
            c.getDateCreation()
        );
    }
}