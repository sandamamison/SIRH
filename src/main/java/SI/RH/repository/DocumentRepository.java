package SI.RH.repository;

import SI.RH.entity.Document;
import SI.RH.entity.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEmployeId(Long employeId);
    List<Document> findByEmployeIdAndTypeDocument(Long employeId, TypeDocument typeDocument);
    List<Document> findByContratId(Integer contratId);
    List<Document> findByDateValiditeBefore(LocalDate date);
}
