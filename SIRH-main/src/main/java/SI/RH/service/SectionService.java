package SI.RH.service;

import SI.RH.dto.SectionRequest;
import SI.RH.dto.SectionResponse;
import SI.RH.entity.Section;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.EmployeRepository;
import SI.RH.repository.SectionRepository;
import SI.RH.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeRepository employeRepository;

    public SectionService(SectionRepository sectionRepository,
                          ServiceRepository serviceRepository,
                          EmployeRepository employeRepository) {
        this.sectionRepository = sectionRepository;
        this.serviceRepository = serviceRepository;
        this.employeRepository = employeRepository;
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> lister(Integer serviceId) {
        List<Section> sections = (serviceId != null)
            ? sectionRepository.findByServiceId(serviceId)
            : sectionRepository.findAll();

        return sections.stream()
            .map(this::versReponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public SectionResponse trouver(Integer id) {
        Section s = sectionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Section", id));
        return versReponse(s);
    }

    public SectionResponse creer(SectionRequest req) {
        if (sectionRepository.existsByCodeSection(req.codeSection())) {
            throw new BusinessException("Une section avec le code " + req.codeSection() + " existe deja.");
        }
        verifierParentEtResponsable(req);

        Section s = new Section();
        appliquer(s, req);
        return versReponse(sectionRepository.save(s));
    }

    public SectionResponse modifier(Integer id, SectionRequest req) {
        Section s = sectionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Section", id));

        if (!s.getCodeSection().equals(req.codeSection()) &&
            sectionRepository.existsByCodeSection(req.codeSection())) {
            throw new BusinessException("Une section avec le code " + req.codeSection() + " existe deja.");
        }
        verifierParentEtResponsable(req);

        appliquer(s, req);
        return versReponse(sectionRepository.save(s));
    }

    public void supprimer(Integer id) {
        if (!sectionRepository.existsById(id)) {
            throw new NotFoundException("Section", id);
        }
        if (employeRepository.existsBySectionId(id)) {
            throw new BusinessException("RG5-07 : impossible de supprimer une section contenant du personnel.");
        }
        sectionRepository.deleteById(id);
    }

    private void verifierParentEtResponsable(SectionRequest req) {
        // RG5-02 : la section appartient a un service existant
        if (!serviceRepository.existsById(req.serviceId())) {
            throw new NotFoundException("Service", req.serviceId());
        }
        // RG5-03 : le responsable doit etre un employe existant
        if (req.employeResponsableId() != null && !employeRepository.existsById(req.employeResponsableId())) {
            throw new NotFoundException("Employe responsable", req.employeResponsableId());
        }
    }

    private void appliquer(Section s, SectionRequest req) {
        s.setCodeSection(req.codeSection());
        s.setNomSection(req.nomSection());
        s.setDescription(req.description());
        s.setServiceId(req.serviceId());
        s.setEmployeResponsableId(req.employeResponsableId());
    }

    public SectionResponse versReponse(Section s) {
        String serviceNom = serviceRepository.findById(s.getServiceId())
            .map(unite -> unite.getNomService())
            .orElse("Inconnu");

        return new SectionResponse(
            s.getId(),
            s.getCodeSection(),
            s.getNomSection(),
            s.getDescription(),
            s.getServiceId(),
            serviceNom,
            s.getEmployeResponsableId()
        );
    }
}
