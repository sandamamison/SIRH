package SI.RH.controller;

import SI.RH.dto.ServiceRequest;
import SI.RH.dto.ServiceResponse;
import SI.RH.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService service;

    public ServiceController(ServiceService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceResponse> lister(@RequestParam(required = false) Integer departementId) {
        return service.lister(departementId);
    }

    @GetMapping("/{id}")
    public ServiceResponse trouver(@PathVariable Integer id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse creer(@Valid @RequestBody ServiceRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    public ServiceResponse modifier(@PathVariable Integer id, @Valid @RequestBody ServiceRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        service.supprimer(id);
    }
}
