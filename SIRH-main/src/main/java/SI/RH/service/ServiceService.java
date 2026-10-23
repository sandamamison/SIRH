package SI.RH.service;

import SI.RH.dto.ServiceRequest;
import SI.RH.dto.ServiceResponse;
import SI.RH.entity.Departement;
import SI.RH.entity.Service;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.DepartementRepository;
import SI.RH.repository.EmployeRepository;
import SI.RH.repository.SectionRepository;
import SI.RH.repository.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service pleinement qualifie : le nom "Service" designe ici l'entite SI.RH.entity.Service
@org.springframework.stereotype.Service
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final DepartementRepository departementRepository;
    private final SectionRepository sectionRepository;
    private final EmployeRepository employeRepository;

    public ServiceService(ServiceRepository serviceRepository,
                          DepartementRepository departementRepository,
                          SectionRepository sectionRepository,
                          EmployeRepository employeRepository) {
        this.serviceRepository = serviceRepository;
        this.departementRepository = departementRepository;
        this.sectionRepository = sectionRepository;
        this.employeRepository = employeRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> lister(Integer departementId) {
        List<Service> services = (departementId != null)
            ? serviceRepository.findByDepartementId(departementId)
            : serviceRepository.findAll();

        return services.stream()
            .map(this::versReponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ServiceResponse trouver(Integer id) {
        Service s = serviceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Service", id));
        return versReponse(s);
    }

    public ServiceResponse creer(ServiceRequest req) {
        if (serviceRepository.existsByCodeService(req.codeService())) {
            throw new BusinessException("Un service avec le code " + req.codeService() + " existe deja.");
        }
        verifierParentEtResponsable(req);

        Service s = new Service();
        appliquer(s, req);
        return versReponse(serviceRepository.save(s));
    }

    public ServiceResponse modifier(Integer id, ServiceRequest req) {
        Service s = serviceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Service", id));

        if (!s.getCodeService().equals(req.codeService()) &&
            serviceRepository.existsByCodeService(req.codeService())) {
            throw new BusinessException("Un service avec le code " + req.codeService() + " existe deja.");
        }
        verifierParentEtResponsable(req);

        appliquer(s, req);
        return versReponse(serviceRepository.save(s));
    }

    public void supprimer(Integer id) {
        if (!serviceRepository.existsById(id)) {
            throw new NotFoundException("Service", id);
        }
        if (sectionRepository.existsByServiceId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer un service contenant des sections.");
        }
        if (employeRepository.existsByServiceId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer un service contenant du personnel.");
        }
        serviceRepository.deleteById(id);
    }

    private void verifierParentEtResponsable(ServiceRequest req) {
        // RG5-02 : le service appartient a un departement existant
        if (!departementRepository.existsById(req.departementId())) {
            throw new NotFoundException("Departement", req.departementId());
        }
        // RG5-03 : le responsable doit etre un employe existant
        if (req.employeResponsableId() != null && !employeRepository.existsById(req.employeResponsableId())) {
            throw new NotFoundException("Employe responsable", req.employeResponsableId());
        }
    }

    private void appliquer(Service s, ServiceRequest req) {
        s.setCodeService(req.codeService());
        s.setNomService(req.nomService());
        s.setDescription(req.description());
        s.setDepartementId(req.departementId());
        s.setEmployeResponsableId(req.employeResponsableId());
    }

    public ServiceResponse versReponse(Service s) {
        String deptNom = departementRepository.findById(s.getDepartementId())
            .map(Departement::getNomDepartement)
            .orElse("Inconnu");

        return new ServiceResponse(
            s.getId(),
            s.getCodeService(),
            s.getNomService(),
            s.getDescription(),
            s.getDepartementId(),
            deptNom,
            s.getEmployeResponsableId()
        );
    }
}
