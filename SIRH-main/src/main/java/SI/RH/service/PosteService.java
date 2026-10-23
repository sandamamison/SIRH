package SI.RH.service;

import SI.RH.dto.PosteRequest;
import SI.RH.dto.PosteResponse;
import SI.RH.entity.Departement;
import SI.RH.entity.Poste;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.DepartementRepository;
import SI.RH.repository.PosteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PosteService {

    private final PosteRepository posteRepository;
    private final DepartementRepository departementRepository;

    public PosteService(PosteRepository posteRepository, DepartementRepository departementRepository) {
        this.posteRepository = posteRepository;
        this.departementRepository = departementRepository;
    }

    @Transactional(readOnly = true)
    public List<PosteResponse> lister(Integer departementId) {
        List<Poste> postes = (departementId != null)
            ? posteRepository.findByDepartementId(departementId)
            : posteRepository.findAll();

        return postes.stream()
            .map(this::versReponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PosteResponse trouver(Integer id) {
        Poste p = posteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Poste", id));
        return versReponse(p);
    }

    public PosteResponse creer(PosteRequest req) {
        if (posteRepository.existsByCodePoste(req.codePoste())) {
            throw new BusinessException("Un poste avec le code " + req.codePoste() + " existe deja.");
        }
        verifierDepartementEtSalaires(req);

        Poste p = new Poste();
        appliquer(p, req);
        return versReponse(posteRepository.save(p));
    }

    public PosteResponse modifier(Integer id, PosteRequest req) {
        Poste p = posteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Poste", id));

        if (!p.getCodePoste().equals(req.codePoste()) && posteRepository.existsByCodePoste(req.codePoste())) {
            throw new BusinessException("Un poste avec le code " + req.codePoste() + " existe deja.");
        }
        verifierDepartementEtSalaires(req);

        appliquer(p, req);
        return versReponse(posteRepository.save(p));
    }

    public void supprimer(Integer id) {
        if (!posteRepository.existsById(id)) {
            throw new NotFoundException("Poste", id);
        }
        posteRepository.deleteById(id);
    }

    private void verifierDepartementEtSalaires(PosteRequest req) {
        if (!departementRepository.existsById(req.departementId())) {
            throw new NotFoundException("Departement", req.departementId());
        }
        if (req.salaireMin() != null && req.salaireMax() != null && req.salaireMax() < req.salaireMin()) {
            throw new BusinessException("Le salaire maximum (" + req.salaireMax() +
                ") ne peut pas etre inferieur au salaire minimum (" + req.salaireMin() + ").");
        }
    }

    private void appliquer(Poste p, PosteRequest req) {
        p.setCodePoste(req.codePoste());
        p.setTitre(req.titre());
        p.setDescriptionMission(req.descriptionMission());
        p.setSalaireMin(req.salaireMin());
        p.setSalaireMax(req.salaireMax());
        p.setDepartementId(req.departementId());
    }

    public PosteResponse versReponse(Poste p) {
        String deptNom = departementRepository.findById(p.getDepartementId())
            .map(Departement::getNomDepartement)
            .orElse("Inconnu");

        return new PosteResponse(
            p.getId(),
            p.getCodePoste(),
            p.getTitre(),
            p.getDescriptionMission(),
            p.getSalaireMin(),
            p.getSalaireMax(),
            p.getDepartementId(),
            deptNom
        );
    }
}
