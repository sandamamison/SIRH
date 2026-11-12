package SI.RH.controller;

import SI.RH.entity.Document;
import SI.RH.entity.enums.TypeDocument;
import SI.RH.service.DocumentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    public record DocumentCreationRequest(
            Long employeId,
            Integer contratId,
            TypeDocument typeDocument,
            String fichierUrl,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateValidite
    ) {}

    @PostMapping
    public ResponseEntity<Document> ajouter(@RequestBody DocumentCreationRequest req) {
        Document doc = documentService.enregistrerDocument(
                req.employeId(),
                req.contratId(),
                req.typeDocument(),
                req.fichierUrl(),
                req.dateValidite()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.trouver(id));
    }

    @GetMapping("/employe/{employeId}")
    public List<Document> obtenirParEmploye(@PathVariable Long employeId) {
        return documentService.getDocumentsParEmploye(employeId);
    }

    @GetMapping("/expires")
    public List<Document> obtenirExpires(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {
        return documentService.getDocumentsExpires(dateReference);
    }
}
