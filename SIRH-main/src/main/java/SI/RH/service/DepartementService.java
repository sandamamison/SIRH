package SI.RH.service;

import SI.RH.dto.DepartementRequest;
import SI.RH.dto.DepartementResponse;
import SI.RH.entity.Departement;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.DepartementRepository;
import SI.RH.repository.EmployeRepository;
import SI.RH.repository.PosteRepository;
import SI.RH.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartementService {

    private final DepartementRepository departementRepository;
    private final PosteRepository posteRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeRepository employeRepository;

    public DepartementService(DepartementRepository departementRepository,
                              PosteRepository posteRepository,
                              ServiceRepository serviceRepository,
                              EmployeRepository employeRepository) {
        this.departementRepository = departementRepository;
        this.posteRepository = posteRepository;
        this.serviceRepository = serviceRepository;
        this.employeRepository = employeRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartementResponse> lister() {
        return departementRepository.findAll().stream()
            .map(this::versReponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public DepartementResponse trouver(Integer id) {
        Departement d = departementRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Departement", id));
        return versReponse(d);
    }

    public DepartementResponse creer(DepartementRequest req) {
        if (departementRepository.existsByCodeDepartement(req.codeDepartement())) {
            throw new BusinessException("Un departement avec le code " + req.codeDepartement() + " existe deja.");
        }
        verifierParent(req.departementParentId(), null);

        Departement d = new Departement();
        appliquer(d, req);
        return versReponse(departementRepository.save(d));
    }

    public DepartementResponse modifier(Integer id, DepartementRequest req) {
        Departement d = departementRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Departement", id));

        if (!d.getCodeDepartement().equals(req.codeDepartement()) &&
            departementRepository.existsByCodeDepartement(req.codeDepartement())) {
            throw new BusinessException("Un departement avec le code " + req.codeDepartement() + " existe deja.");
        }
        verifierParent(req.departementParentId(), id);

        appliquer(d, req);
        return versReponse(departementRepository.save(d));
    }

    public void supprimer(Integer id) {
        if (!departementRepository.existsById(id)) {
            throw new NotFoundException("Departement", id);
        }
        // RG5-07 : un departement non vide n'est pas supprimable
        if (posteRepository.existsByDepartementId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer un departement contenant des postes.");
        }
        if (serviceRepository.existsByDepartementId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer un departement contenant des services.");
        }
        if (employeRepository.existsByDepartementId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer un departement contenant du personnel.");
        }
        departementRepository.deleteById(id);
    }

    private void verifierParent(Integer parentId, Integer currentId) {
        if (parentId != null) {
            if (currentId != null && parentId.equals(currentId)) {
                throw new BusinessException("Un departement ne peut pas etre son propre parent.");
            }
            if (!departementRepository.existsById(parentId)) {
                throw new NotFoundException("Departement parent", parentId);
            }
        }
    }

    private void appliquer(Departement d, DepartementRequest req) {
        d.setCodeDepartement(req.codeDepartement());
        d.setNomDepartement(req.nomDepartement());
        d.setDescription(req.description());
        d.setEmployeResponsableId(req.employeResponsableId());
        d.setDepartementParentId(req.departementParentId());
    }

    public DepartementResponse versReponse(Departement d) {
        return new DepartementResponse(
            d.getId(),
            d.getCodeDepartement(),
            d.getNomDepartement(),
            d.getDescription(),
            d.getEmployeResponsableId(),
            d.getDepartementParentId()
        );
    }
}
