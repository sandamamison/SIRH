package SI.RH.dto;

import SI.RH.entity.enums.StatutContrat;
import SI.RH.entity.enums.TypeContrat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratResponse(
    Integer id,
    String numeroContrat,
    Integer employeId,
    TypeContrat typeContrat,
    LocalDate dateDebut,
    LocalDate dateFin,
    Integer salaireBrut,
    StatutContrat statut,
    Integer contratPrecedentId,
    Integer employeCreateurId,
    Integer employeSignataireId,
    LocalDate dateSignature,
    LocalDateTime dateCreation
) {}