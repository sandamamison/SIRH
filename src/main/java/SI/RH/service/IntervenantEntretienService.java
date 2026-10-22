package SI.RH.service;

import SI.RH.entity.IntervenantEntretien;
import SI.RH.repository.IntervenantEntretienRepository;
import SI.RH.repository.EntretienRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class IntervenantEntretienService {
    private final IntervenantEntretienRepository repository;
    private final EntretienRepository entretienRepository;

    public IntervenantEntretienService(
            IntervenantEntretienRepository repository,
            EntretienRepository entretienRepository) {
        this.repository = repository;
        this.entretienRepository = entretienRepository;
    }

    public IntervenantEntretien creer(IntervenantEntretien intervenant) {
        if (intervenant.getEntretienId() == null || intervenant.getEmployeId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'entretien et l'employé sont obligatoires");

        if (!entretienRepository.existsById(intervenant.getEntretienId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entretien introuvable");

        if (repository.existsByEntretienIdAndEmployeId(
                intervenant.getEntretienId(), intervenant.getEmployeId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet employé participe déjà à cet entretien");

        intervenant.setId(null);
        return repository.save(intervenant);
    }

    @Transactional(readOnly = true)
    public List<IntervenantEntretien> lister() {
        return repository.findAll();
    }

    public void supprimer(Long id) {
        IntervenantEntretien intervenant = repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Intervenant introuvable"));
        repository.delete(intervenant);
    }
}
