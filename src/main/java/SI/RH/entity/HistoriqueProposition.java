package SI.RH.entity;

import SI.RH.entity.enums.StatutProposition;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_propositions")
public class HistoriqueProposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long propositionId;
    @Enumerated(EnumType.STRING)
    private StatutProposition ancienStatut;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProposition nouveauStatut;
    @Column(nullable = false)
    private LocalDateTime dateTransition;
    private String auteur;
    private String commentaire;

    protected HistoriqueProposition() {
    }

    public HistoriqueProposition(Long p, StatutProposition a, StatutProposition n, String u, String c) {
        propositionId = p;
        ancienStatut = a;
        nouveauStatut = n;
        auteur = u;
        commentaire = c;
        dateTransition = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getPropositionId() {
        return propositionId;
    }

    public StatutProposition getAncienStatut() {
        return ancienStatut;
    }

    public StatutProposition getNouveauStatut() {
        return nouveauStatut;
    }

    public LocalDateTime getDateTransition() {
        return dateTransition;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getCommentaire() {
        return commentaire;
    }
}
