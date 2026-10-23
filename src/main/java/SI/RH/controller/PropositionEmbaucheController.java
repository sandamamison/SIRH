package SI.RH.controller;

import SI.RH.dto.*;
import SI.RH.entity.*;
import SI.RH.entity.enums.StatutProposition;
import SI.RH.service.PropositionEmbaucheService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;

@RestController
@RequestMapping("/api/propositions-embauche")
public class PropositionEmbaucheController {
    private final PropositionEmbaucheService service;

    public PropositionEmbaucheController(PropositionEmbaucheService s) {
        service = s;
    }

    @PostMapping
    public ResponseEntity<PropositionResponse> create(@RequestBody PropositionCreateRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(service.envoyer(r.getCandidatureId(), r.getDateExpiration(), r.getCommentaire(), r.getCreatedBy())));
    }

    @GetMapping
    public List<PropositionResponse> list() {
        return service.list().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PropositionResponse get(@PathVariable Long id) {
        return toResponse(service.get(id));
    }

    @GetMapping("/{id}/historique")
    public List<HistoriqueProposition> history(@PathVariable Long id) {
        return service.historique(id);
    }

    @PostMapping("/{id}/accepter")
    public EmployeResponse accept(@PathVariable Long id, @RequestParam(required = false) String auteur, Authentication authentication) {
        Employe e = service.accepter(id, auteur, authentication.getName());
        return new EmployeResponse(e.getId(), e.getMatricule(), e.getPersonneId(), e.getDateEmbauche(), e.getStatut());
    }

    @PostMapping("/{id}/refuser")
    public PropositionResponse refuse(@PathVariable Long id, @RequestParam(required = false) String auteur, Authentication authentication) {
        return toResponse(service.repondre(id, StatutProposition.REFUSEE, auteur, authentication.getName()));
    }

    @PostMapping("/{id}/expirer")
    public PropositionResponse expire(@PathVariable Long id, @RequestParam(required = false) String auteur) {
        return toResponse(service.repondre(id, StatutProposition.EXPIREE, auteur));
    }

    private PropositionResponse toResponse(PropositionEmbauche p) {
        return new PropositionResponse(p.getId(), p.getCandidatureId(), p.getStatut(), p.getDateEnvoi(), p.getDateReponse(), p.getDateExpiration(), p.getCommentaire(), p.getCreatedBy());
    }
}
