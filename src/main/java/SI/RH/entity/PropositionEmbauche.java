package SI.RH.entity;
import SI.RH.entity.enums.StatutProposition; import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="propositions_embauche")
public class PropositionEmbauche {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="candidature_id",nullable=false) private Long candidatureId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private StatutProposition statut;
 private LocalDateTime dateEnvoi,dateReponse; private LocalDate dateExpiration; private String commentaire,createdBy;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt; private LocalDateTime updatedAt;
 public PropositionEmbauche() {}
 public Long getId(){return id;} public void setId(Long v){id=v;} public Long getCandidatureId(){return candidatureId;} public void setCandidatureId(Long v){candidatureId=v;}
 public StatutProposition getStatut(){return statut;} public void setStatut(StatutProposition v){statut=v;} public LocalDateTime getDateEnvoi(){return dateEnvoi;} public void setDateEnvoi(LocalDateTime v){dateEnvoi=v;} public LocalDateTime getDateReponse(){return dateReponse;} public void setDateReponse(LocalDateTime v){dateReponse=v;} public LocalDate getDateExpiration(){return dateExpiration;} public void setDateExpiration(LocalDate v){dateExpiration=v;} public String getCommentaire(){return commentaire;} public void setCommentaire(String v){commentaire=v;} public String getCreatedBy(){return createdBy;} public void setCreatedBy(String v){createdBy=v;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;} public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
 @PrePersist void create(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;} @PreUpdate void update(){updatedAt=LocalDateTime.now();}
}
