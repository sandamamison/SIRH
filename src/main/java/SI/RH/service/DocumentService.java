package SI.RH.service;

import SI.RH.entity.Document;
import SI.RH.entity.enums.TypeDocument;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document enregistrerDocument(Long employeId, Integer contratId, TypeDocument typeDocument,
                                        String fichierUrl, LocalDate dateValidite) {
        Document doc = new Document();
        doc.setEmployeId(employeId);
        doc.setContratId(contratId);
        doc.setTypeDocument(typeDocument);
        doc.setFichierUrl(fichierUrl);
        doc.setDateDepot(LocalDate.now());
        doc.setDateValidite(dateValidite);
        return documentRepository.save(doc);
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsParEmploye(Long employeId) {
        return documentRepository.findByEmployeId(employeId);
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsExpires(LocalDate dateReference) {
        LocalDate ref = dateReference != null ? dateReference : LocalDate.now();
        return documentRepository.findByDateValiditeBefore(ref);
    }

    @Transactional(readOnly = true)
    public Document trouver(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document", id));
    }
}
