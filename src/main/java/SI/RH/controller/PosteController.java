package SI.RH.controller;

import SI.RH.dto.PosteRequest;
import SI.RH.dto.PosteResponse;
import SI.RH.service.PosteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postes")
public class PosteController {

    private final PosteService service;

    public PosteController(PosteService service) {
        this.service = service;
    }

    @GetMapping
    public List<PosteResponse> lister(@RequestParam(required = false) Integer departementId) {
        return service.lister(departementId);
    }

    @GetMapping("/{id}")
    public PosteResponse trouver(@PathVariable Integer id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PosteResponse creer(@Valid @RequestBody PosteRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    public PosteResponse modifier(@PathVariable Integer id, @Valid @RequestBody PosteRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        service.supprimer(id);
    }
}
