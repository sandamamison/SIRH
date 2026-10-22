package SI.RH.controller;

import SI.RH.dto.DepartementRequest;
import SI.RH.dto.DepartementResponse;
import SI.RH.service.DepartementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
public class DepartementController {

    private final DepartementService service;

    public DepartementController(DepartementService service) {
        this.service = service;
    }

    @GetMapping
    public List<DepartementResponse> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public DepartementResponse trouver(@PathVariable Integer id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartementResponse creer(@Valid @RequestBody DepartementRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    public DepartementResponse modifier(@PathVariable Integer id, @Valid @RequestBody DepartementRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        service.supprimer(id);
    }
}
