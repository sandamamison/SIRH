package SI.RH.controller;

import SI.RH.dto.EmployeRequest;
import SI.RH.dto.EmployeResponse;
import SI.RH.service.EmployeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employes")
public class EmployeController {

    private final EmployeService service;

    public EmployeController(EmployeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeResponse> lister(@RequestParam(required = false) Integer departementId,
                                        @RequestParam(required = false) Integer serviceId,
                                        @RequestParam(required = false) Integer sectionId) {
        return service.lister(departementId, serviceId, sectionId);
    }

    @GetMapping("/{id}")
    public EmployeResponse trouver(@PathVariable Integer id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeResponse creer(@Valid @RequestBody EmployeRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    public EmployeResponse modifier(@PathVariable Integer id, @Valid @RequestBody EmployeRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        service.supprimer(id);
    }
}
