package SI.RH.service;

import SI.RH.dto.EmployeRequest;
import SI.RH.dto.EmployeResponse;
import SI.RH.entity.Departement;
import SI.RH.entity.Employe;
import SI.RH.entity.Section;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.DepartementRepository;
import SI.RH.repository.EmployeRepository;
import SI.RH.repository.SectionRepository;
import SI.RH.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final DepartementRepository departementRepository;
    private final ServiceRepository serviceRepository;
    private final SectionRepository sectionRepository;

    public EmployeService(EmployeRepository employeRepository,
                          DepartementRepository departementRepository,
                          ServiceRepository serviceRepository,
                          SectionRepository sectionRepository) {
        this.employeRepository = employeRepository;
        this.departementRepository = departementRepository;
        this.serviceRepository = serviceRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeResponse> lister(Integer departementId, Integer serviceId, Integer sectionId) {
        int filtres = (departementId != null ? 1 : 0)
            + (serviceId != null ? 1 : 0)
            + (sectionId != null ? 1 : 0);
        if (filtres > 1) {
            throw new BusinessException("Un seul filtre de structure a la fois (departementId, serviceId ou sectionId).");
        }

        List<Employe> employes;
        if (sectionId != null) {
            employes = employeRepository.findBySectionId(sectionId);
        } else if (serviceId != null) {
            employes = employeRepository.findByServiceId(serviceId);
        } else if (departementId != null) {
            employes = employeRepository.findByDepartementId(departementId);
        } else {
            employes = employeRepository.findAll();
        }

        return employes.stream()
            .map(this::versReponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public EmployeResponse trouver(Integer id) {
        Employe e = employeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Employe", id));
        return versReponse(e);
    }

    public EmployeResponse creer(EmployeRequest req) {
        verifierUnicite(req, null);
        verifierRattachement(req);
        verifierManager(req.employeManagerId(), null);

        Employe e = new Employe();
        appliquer(e, req);
        e.setActif(req.actif() == null || req.actif());
        return versReponse(employeRepository.save(e));
    }

    public EmployeResponse modifier(Integer id, EmployeRequest req) {
        Employe e = employeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Employe", id));

        verifierUnicite(req, e);
        verifierRattachement(req);
        verifierManager(req.employeManagerId(), id);

        appliquer(e, req);
        if (req.actif() != null) {
            e.setActif(req.actif());
        }
        return versReponse(employeRepository.save(e));
    }

    public void supprimer(Integer id) {
        if (!employeRepository.existsById(id)) {
            throw new NotFoundException("Employe", id);
        }
        if (employeRepository.existsByEmployeManagerId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer un employe qui manage d'autres employes. Reaffectez-les ou desactivez-le.");
        }
        if (departementRepository.existsByEmployeResponsableId(id)
            || serviceRepository.existsByEmployeResponsableId(id)
            || sectionRepository.existsByEmployeResponsableId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer un employe responsable d'une unite. Changez le responsable ou desactivez-le.");
        }
        employeRepository.deleteById(id);
    }

    // RG5-05 : matricule et email uniques
    private void verifierUnicite(EmployeRequest req, Employe courant) {
        if (employeRepository.existsByMatricule(req.matricule())
            && (courant == null || !courant.getMatricule().equals(req.matricule()))) {
            throw new BusinessException("Un employe avec le matricule " + req.matricule() + " existe deja.");
        }
        if (req.email() != null && employeRepository.existsByEmail(req.email())
            && (courant == null || !req.email().equals(courant.getEmail()))) {
            throw new BusinessException("Un employe avec l'email " + req.email() + " existe deja.");
        }
    }

    // RG5-04 : rattachement a exactement une unite organisationnelle existante
    private void verifierRattachement(EmployeRequest req) {
        int nbUnites = (req.departementId() != null ? 1 : 0)
            + (req.serviceId() != null ? 1 : 0)
            + (req.sectionId() != null ? 1 : 0);
        if (nbUnites != 1) {
            throw new BusinessException("RG5-04 : un employe doit etre rattache a exactement une unite (departementId, serviceId ou sectionId).");
        }
        if (req.departementId() != null && !departementRepository.existsById(req.departementId())) {
            throw new NotFoundException("Departement", req.departementId());
        }
        if (req.serviceId() != null && !serviceRepository.existsById(req.serviceId())) {
            throw new NotFoundException("Service", req.serviceId());
        }
        if (req.sectionId() != null && !sectionRepository.existsById(req.sectionId())) {
            throw new NotFoundException("Section", req.sectionId());
        }
    }

    // RG5-06 : le manager existe et n'est pas l'employe lui-meme
    private void verifierManager(Integer managerId, Integer currentId) {
        if (managerId == null) {
            return;
        }
        if (managerId.equals(currentId)) {
            throw new BusinessException("RG5-06 : un employe ne peut pas etre son propre manager.");
        }
        if (!employeRepository.existsById(managerId)) {
            throw new NotFoundException("Employe manager", managerId);
        }
    }

    private void appliquer(Employe e, EmployeRequest req) {
        e.setMatricule(req.matricule());
        e.setNom(req.nom());
        e.setPrenom(req.prenom());
        e.setEmail(req.email());
        e.setTelephone(req.telephone());
        e.setDateEmbauche(req.dateEmbauche());
        e.setEmployeManagerId(req.employeManagerId());
        e.setDepartementId(req.departementId());
        e.setServiceId(req.serviceId());
        e.setSectionId(req.sectionId());
    }

    public EmployeResponse versReponse(Employe e) {
        String uniteType;
        String uniteNom;
        if (e.getSectionId() != null) {
            uniteType = "SECTION";
            uniteNom = sectionRepository.findById(e.getSectionId())
                .map(Section::getNomSection).orElse("Inconnu");
        } else if (e.getServiceId() != null) {
            uniteType = "SERVICE";
            uniteNom = serviceRepository.findById(e.getServiceId())
                .map(unite -> unite.getNomService()).orElse("Inconnu");
        } else {
            uniteType = "DEPARTEMENT";
            uniteNom = departementRepository.findById(e.getDepartementId())
                .map(Departement::getNomDepartement).orElse("Inconnu");
        }

        return new EmployeResponse(
            e.getId(),
            e.getMatricule(),
            e.getNom(),
            e.getPrenom(),
            e.getEmail(),
            e.getTelephone(),
            e.getDateEmbauche(),
            e.isActif(),
            e.getEmployeManagerId(),
            e.getDepartementId(),
            e.getServiceId(),
            e.getSectionId(),
            uniteType,
            uniteNom
        );
    }
}
