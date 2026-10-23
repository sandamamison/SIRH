package SI.RH.controller;

import SI.RH.dto.SectionRequest;
import SI.RH.dto.SectionResponse;
import SI.RH.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService service;

    public SectionController(SectionService service) {
        this.service = service;
    }

    @GetMapping
    public List<SectionResponse> lister(@RequestParam(required = false) Integer serviceId) {
        return service.lister(serviceId);
    }

    @GetMapping("/{id}")
    public SectionResponse trouver(@PathVariable Integer id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SectionResponse creer(@Valid @RequestBody SectionRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    public SectionResponse modifier(@PathVariable Integer id, @Valid @RequestBody SectionRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        service.supprimer(id);
    }
}
