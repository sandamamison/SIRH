# Plan de modifications détaillé — SIRH Blocs 4 → 7

> **Ref commit** : `9703a1c` | **Stack** : Spring Boot · JPA · PostgreSQL · package `SI.RH`
> Aucun fichier existant n'est supprimé. Les docs restent en place.

---

## 1. Fichier à modifier : `Employe.java`

**Pourquoi** : ajouter les colonnes `departementId` et `posteId` manquantes pour le dossier salarié, et préparer les relations vers les nouveaux blocs via des champs FK simples (même convention que `personneId`).

```java
// Ajouter dans Employe.java après "private StatutEmploye statut;"

// --- Bloc 5 : Dossier salarié ---
@Column
private Long departementId;   // rattachement organisationnel direct

// --- (getters / setters à ajouter) ---
public Long getDepartementId() { return departementId; }
public void setDepartementId(Long departementId) { this.departementId = departementId; }
```

> [!NOTE]
> On ne met PAS de `@OneToMany` vers Onboarding, Document, etc. : on garde la convention FK scalaire du projet (`employeId` dans chaque table fille). Pas de cascade JPA = pas de risque de suppressions accidentelles.

---

## 2. Fichier à modifier : `ContratService.java`

**Pourquoi** : déclencher automatiquement la création de l'onboarding quand un contrat est signé (RG4-05).

```java
// En haut du fichier, ajouter l'import et l'injection :
// import SI.RH.service.OnboardingService;

// Dans le constructeur :
// private final OnboardingService onboardingService;
// public ContratService(ContratRepository contratRepository, OnboardingService onboardingService) {
//     this.contratRepository = contratRepository;
//     this.onboardingService = onboardingService;
// }

// Dans la méthode changerStatut(Contrat c, StatutContrat cible, ...) — après contratRepository.save(c) :
if (cible == StatutContrat.SIGNE) {
    // RG4-05 : création automatique de l'onboarding à la signature
    onboardingService.creerOnboardingAutomatique(c.getEmployeId().longValue());
}
```

> [!IMPORTANT]
> Utiliser `@Lazy` sur `OnboardingService` pour éviter la dépendance circulaire si `OnboardingService` injecte lui aussi `ContratService`.
> ```java
> public ContratService(ContratRepository r, @Lazy OnboardingService o) { ... }
> ```

---

## 3. Fichier à modifier : `base_complete.sql`

**Pourquoi** : ajouter les tables des blocs 4 (onboarding/document), 5 (audit), 6 (temps), 7 (congés) à la suite du script existant, **avant le `COMMIT`**.

Ajouter dans la section `DROP TABLE IF EXISTS` (au début) :

```sql
DROP TABLE IF EXISTS
    absence,
    demande_conge,
    solde_conge,
    droit_conge,
    type_conge,
    anomalie_pointage,
    pointage,
    planning,
    horaire,
    regime_travail,
    journal_audit,
    document,
    onboarding_tache,
    onboarding
    CASCADE;
```

Puis ajouter les tables (voir sections SQL ci-dessous) avant le `COMMIT;` final.

---

## 4. Nouveaux fichiers Java — Bloc 4 : Onboarding & Document

### `entity/Onboarding.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.StatutOnboarding;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "onboarding")
public class Onboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutOnboarding statut = StatutOnboarding.EN_COURS;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Onboarding() {}

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public StatutOnboarding getStatut() { return statut; }
    public void setStatut(StatutOnboarding statut) { this.statut = statut; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

### `entity/OnboardingTache.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.StatutTache;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "onboarding_tache")
public class OnboardingTache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long onboardingId;

    @Column(nullable = false, length = 150)
    private String libelle;

    private Long responsableId;   // employeId du responsable

    private LocalDate echeance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutTache statut = StatutTache.A_FAIRE;

    @Column(nullable = false)
    private Boolean obligatoire = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OnboardingTache() {}

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOnboardingId() { return onboardingId; }
    public void setOnboardingId(Long onboardingId) { this.onboardingId = onboardingId; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }
    public LocalDate getEcheance() { return echeance; }
    public void setEcheance(LocalDate echeance) { this.echeance = echeance; }
    public StatutTache getStatut() { return statut; }
    public void setStatut(StatutTache statut) { this.statut = statut; }
    public Boolean getObligatoire() { return obligatoire; }
    public void setObligatoire(Boolean obligatoire) { this.obligatoire = obligatoire; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### `entity/Document.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.TypeDocument;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeDocument typeDocument;

    @Column(nullable = false, length = 255)
    private String fichierUrl;

    @Column(nullable = false)
    private Long employeId;

    private Long contratId;          // null si rattaché à l'employé seulement

    @Column(nullable = false)
    private LocalDate dateDepot;

    private LocalDate dateValidite;  // null = pas d'expiration

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Document() {}

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TypeDocument getTypeDocument() { return typeDocument; }
    public void setTypeDocument(TypeDocument typeDocument) { this.typeDocument = typeDocument; }
    public String getFichierUrl() { return fichierUrl; }
    public void setFichierUrl(String fichierUrl) { this.fichierUrl = fichierUrl; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getContratId() { return contratId; }
    public void setContratId(Long contratId) { this.contratId = contratId; }
    public LocalDate getDateDepot() { return dateDepot; }
    public void setDateDepot(LocalDate dateDepot) { this.dateDepot = dateDepot; }
    public LocalDate getDateValidite() { return dateValidite; }
    public void setDateValidite(LocalDate dateValidite) { this.dateValidite = dateValidite; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### Enums Bloc 4

**`entity/enums/StatutOnboarding.java`**
```java
package SI.RH.entity.enums;
public enum StatutOnboarding { EN_COURS, TERMINE, ANNULE }
```

**`entity/enums/StatutTache.java`**
```java
package SI.RH.entity.enums;
public enum StatutTache { A_FAIRE, EN_COURS, FAIT }
```

**`entity/enums/TypeDocument.java`**
```java
package SI.RH.entity.enums;
public enum TypeDocument { CONTRAT_SIGNE, PIECE_IDENTITE, DIPLOME, JUSTIFICATIF, AUTRE }
```

---

## 5. Nouveaux fichiers Java — Bloc 5 : Dossier salarié & Audit

### `entity/JournalAudit.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.TypeAudit;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long acteurId;        // employeId ou utilisateurId de celui qui agit

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeAudit typeAction;

    @Column(nullable = false, length = 100)
    private String tableCible;    // ex: "employes", "contrat"

    private Long enregistrementId; // id de la ligne concernée

    @Column(columnDefinition = "TEXT")
    private String detail;         // JSON ou texte libre décrivant le changement

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateAction;

    public JournalAudit() {}

    @PrePersist
    void onCreate() { dateAction = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public Long getActeurId() { return acteurId; }
    public void setActeurId(Long acteurId) { this.acteurId = acteurId; }
    public TypeAudit getTypeAction() { return typeAction; }
    public void setTypeAction(TypeAudit typeAction) { this.typeAction = typeAction; }
    public String getTableCible() { return tableCible; }
    public void setTableCible(String tableCible) { this.tableCible = tableCible; }
    public Long getEnregistrementId() { return enregistrementId; }
    public void setEnregistrementId(Long enregistrementId) { this.enregistrementId = enregistrementId; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public LocalDateTime getDateAction() { return dateAction; }
}
```

### Enum Bloc 5

**`entity/enums/TypeAudit.java`**
```java
package SI.RH.entity.enums;
public enum TypeAudit { CONSULTATION, MODIFICATION, CREATION, SUPPRESSION, CONNEXION }
```

---

## 6. Nouveaux fichiers Java — Bloc 6 : Temps de travail & Pointage

### `entity/RegimeTravail.java`

```java
package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "regime_travail")
public class RegimeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String libelle;

    @Column(nullable = false)
    private Integer nbHeuresSemaine;   // ex: 35, 39

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Integer getNbHeuresSemaine() { return nbHeuresSemaine; }
    public void setNbHeuresSemaine(Integer nbHeuresSemaine) { this.nbHeuresSemaine = nbHeuresSemaine; }
}
```

### `entity/Horaire.java`

```java
package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horaire")
public class Horaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String libelle;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    private Integer pauseMinutes;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public Integer getPauseMinutes() { return pauseMinutes; }
    public void setPauseMinutes(Integer pauseMinutes) { this.pauseMinutes = pauseMinutes; }
}
```

### `entity/Planning.java`

```java
package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Une semaine de planning pour un employé : rattachement à un horaire. */
@Entity
@Table(name = "planning",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "semaine_debut"}))
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @Column(name = "horaire_id", nullable = false)
    private Long horaireId;

    @Column(name = "semaine_debut", nullable = false)
    private LocalDate semaineDebut;   // toujours un lundi

    @Column(name = "regime_travail_id")
    private Long regimeTravailId;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getHoraireId() { return horaireId; }
    public void setHoraireId(Long horaireId) { this.horaireId = horaireId; }
    public LocalDate getSemaineDebut() { return semaineDebut; }
    public void setSemaineDebut(LocalDate semaineDebut) { this.semaineDebut = semaineDebut; }
    public Long getRegimeTravailId() { return regimeTravailId; }
    public void setRegimeTravailId(Long regimeTravailId) { this.regimeTravailId = regimeTravailId; }
}
```

### `entity/Pointage.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.DirectionPointage;
import SI.RH.entity.enums.OriginePointage;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pointage")
public class Pointage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DirectionPointage direction;    // ENTREE ou SORTIE

    @Column(nullable = false)
    private LocalDateTime horodatage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OriginePointage origine;        // BADGE, APPLI, MANUEL

    private Long planningId;               // planning de référence (nullable)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Pointage() {}

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public DirectionPointage getDirection() { return direction; }
    public void setDirection(DirectionPointage direction) { this.direction = direction; }
    public LocalDateTime getHorodatage() { return horodatage; }
    public void setHorodatage(LocalDateTime horodatage) { this.horodatage = horodatage; }
    public OriginePointage getOrigine() { return origine; }
    public void setOrigine(OriginePointage origine) { this.origine = origine; }
    public Long getPlanningId() { return planningId; }
    public void setPlanningId(Long planningId) { this.planningId = planningId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### `entity/AnomaliePointage.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.StatutAnomalie;
import SI.RH.entity.enums.TypeAnomalie;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomalie_pointage")
public class AnomaliePointage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    private Long pointageId;    // pointage déclencheur (peut être null si absence détectée)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TypeAnomalie typeAnomalie;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutAnomalie statut = StatutAnomalie.DETECTEE;

    private String commentaireJustification;

    @Column(nullable = false, updatable = false)
    private LocalDateTime detectedAt;

    public AnomaliePointage() {}

    @PrePersist
    void onCreate() { detectedAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getPointageId() { return pointageId; }
    public void setPointageId(Long pointageId) { this.pointageId = pointageId; }
    public TypeAnomalie getTypeAnomalie() { return typeAnomalie; }
    public void setTypeAnomalie(TypeAnomalie typeAnomalie) { this.typeAnomalie = typeAnomalie; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public StatutAnomalie getStatut() { return statut; }
    public void setStatut(StatutAnomalie statut) { this.statut = statut; }
    public String getCommentaireJustification() { return commentaireJustification; }
    public void setCommentaireJustification(String c) { this.commentaireJustification = c; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
}
```

### Enums Bloc 6

**`entity/enums/DirectionPointage.java`**
```java
package SI.RH.entity.enums;
public enum DirectionPointage { ENTREE, SORTIE }
```

**`entity/enums/OriginePointage.java`**
```java
package SI.RH.entity.enums;
public enum OriginePointage { BADGE, APPLI, MANUEL }
```

**`entity/enums/StatutAnomalie.java`**
```java
package SI.RH.entity.enums;
public enum StatutAnomalie { DETECTEE, JUSTIFIEE, IGNOREE }
```

**`entity/enums/TypeAnomalie.java`**
```java
package SI.RH.entity.enums;
public enum TypeAnomalie { ENTREE_SANS_SORTIE, HORS_PLANNING, RETARD, HEURE_SUP_NON_JUSTIFIEE }
```

---

## 7. Nouveaux fichiers Java — Bloc 7 : Congés & Absences

### `entity/TypeConge.java`

```java
package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "type_conge")
public class TypeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;        // ex: "CA", "RTT", "MALADIE"

    @Column(nullable = false, length = 100)
    private String libelle;

    @Column(nullable = false)
    private Boolean plafonne = false;

    private Integer dureeMaxJours;   // null si non plafonné

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Boolean getPlafonne() { return plafonne; }
    public void setPlafonne(Boolean plafonne) { this.plafonne = plafonne; }
    public Integer getDureeMaxJours() { return dureeMaxJours; }
    public void setDureeMaxJours(Integer dureeMaxJours) { this.dureeMaxJours = dureeMaxJours; }
}
```

### `entity/DroitConge.java`

```java
package SI.RH.entity;

import jakarta.persistence.*;

/** Jours acquis par un employé pour un type de congé et une année donnée. */
@Entity
@Table(name = "droit_conge",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "type_conge_id", "annee"}))
public class DroitConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @Column(name = "type_conge_id", nullable = false)
    private Long typeCongeId;

    @Column(nullable = false)
    private Integer annee;

    @Column(nullable = false)
    private Double joursAcquis;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getTypeCongeId() { return typeCongeId; }
    public void setTypeCongeId(Long typeCongeId) { this.typeCongeId = typeCongeId; }
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public Double getJoursAcquis() { return joursAcquis; }
    public void setJoursAcquis(Double joursAcquis) { this.joursAcquis = joursAcquis; }
}
```

### `entity/SoldeConge.java`

```java
package SI.RH.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Solde courant (calculé ou maintenu) par employé et type de congé. */
@Entity
@Table(name = "solde_conge",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "type_conge_id"}))
public class SoldeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @Column(name = "type_conge_id", nullable = false)
    private Long typeCongeId;

    @Column(nullable = false)
    private Double soldeCourant = 0.0;

    private LocalDateTime updatedAt;

    public SoldeConge() {}

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getTypeCongeId() { return typeCongeId; }
    public void setTypeCongeId(Long typeCongeId) { this.typeCongeId = typeCongeId; }
    public Double getSoldeCourant() { return soldeCourant; }
    public void setSoldeCourant(Double soldeCourant) { this.soldeCourant = soldeCourant; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

### `entity/DemandeConge.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.StatutDemandeConge;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_conge")
public class DemandeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Column(nullable = false)
    private Long typeCongeId;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatutDemandeConge statut = StatutDemandeConge.BROUILLON;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private Long validateurManagerId;
    private LocalDateTime dateValidationManager;
    private Long validateurRhId;
    private LocalDateTime dateValidationRh;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DemandeConge() {}

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getTypeCongeId() { return typeCongeId; }
    public void setTypeCongeId(Long typeCongeId) { this.typeCongeId = typeCongeId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public StatutDemandeConge getStatut() { return statut; }
    public void setStatut(StatutDemandeConge statut) { this.statut = statut; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public Long getValidateurManagerId() { return validateurManagerId; }
    public void setValidateurManagerId(Long id) { this.validateurManagerId = id; }
    public LocalDateTime getDateValidationManager() { return dateValidationManager; }
    public void setDateValidationManager(LocalDateTime d) { this.dateValidationManager = d; }
    public Long getValidateurRhId() { return validateurRhId; }
    public void setValidateurRhId(Long id) { this.validateurRhId = id; }
    public LocalDateTime getDateValidationRh() { return dateValidationRh; }
    public void setDateValidationRh(LocalDateTime d) { this.dateValidationRh = d; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### `entity/Absence.java`

```java
package SI.RH.entity;

import SI.RH.entity.enums.TypeAbsence;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "absence")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeAbsence typeAbsence;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    private Long demandeCongeId;      // null si absence non planifiée

    private String justificatifUrl;   // pièce jointe (arrêt maladie, etc.)

    @Column(nullable = false)
    private Boolean justifiee = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Absence() {}

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public TypeAbsence getTypeAbsence() { return typeAbsence; }
    public void setTypeAbsence(TypeAbsence typeAbsence) { this.typeAbsence = typeAbsence; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public Long getDemandeCongeId() { return demandeCongeId; }
    public void setDemandeCongeId(Long demandeCongeId) { this.demandeCongeId = demandeCongeId; }
    public String getJustificatifUrl() { return justificatifUrl; }
    public void setJustificatifUrl(String justificatifUrl) { this.justificatifUrl = justificatifUrl; }
    public Boolean getJustifiee() { return justifiee; }
    public void setJustifiee(Boolean justifiee) { this.justifiee = justifiee; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### Enums Bloc 7

**`entity/enums/StatutDemandeConge.java`**
```java
package SI.RH.entity.enums;
public enum StatutDemandeConge {
    BROUILLON, SOUMISE, VALIDEE_MANAGER, VALIDEE_RH, REFUSEE, ANNULEE;

    public boolean peutPasserA(StatutDemandeConge cible) {
        return switch (this) {
            case BROUILLON       -> cible == SOUMISE;
            case SOUMISE         -> cible == VALIDEE_MANAGER || cible == REFUSEE || cible == ANNULEE;
            case VALIDEE_MANAGER -> cible == VALIDEE_RH || cible == REFUSEE;
            case VALIDEE_RH      -> cible == ANNULEE;   // annulation RH après validation exceptionnelle
            default              -> false;
        };
    }
}
```

**`entity/enums/TypeAbsence.java`**
```java
package SI.RH.entity.enums;
public enum TypeAbsence { CONGE_ANNUEL, RTT, MALADIE, SANS_SOLDE, AUTRE }
```

---

## 8. SQL — nouveaux blocs à ajouter dans `base_complete.sql`

Bloc à insérer **juste avant `COMMIT;`** dans `base_complete.sql` :

```sql
-- ============================================================
-- BLOC 4 : Onboarding & Documents
-- ============================================================

CREATE TABLE onboarding (
    id          BIGSERIAL PRIMARY KEY,
    employe_id  BIGINT      NOT NULL REFERENCES employes(id),
    date_debut  DATE        NOT NULL,
    statut      VARCHAR(20) NOT NULL DEFAULT 'EN_COURS'
        CHECK (statut IN ('EN_COURS', 'TERMINE', 'ANNULE')),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE onboarding_tache (
    id              BIGSERIAL PRIMARY KEY,
    onboarding_id   BIGINT      NOT NULL REFERENCES onboarding(id) ON DELETE CASCADE,
    libelle         VARCHAR(150) NOT NULL,
    responsable_id  BIGINT REFERENCES employes(id),
    echeance        DATE,
    statut          VARCHAR(20) NOT NULL DEFAULT 'A_FAIRE'
        CHECK (statut IN ('A_FAIRE', 'EN_COURS', 'FAIT')),
    obligatoire     BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE document (
    id              BIGSERIAL PRIMARY KEY,
    type_document   VARCHAR(30) NOT NULL
        CHECK (type_document IN ('CONTRAT_SIGNE','PIECE_IDENTITE','DIPLOME','JUSTIFICATIF','AUTRE')),
    fichier_url     VARCHAR(255) NOT NULL,
    employe_id      BIGINT      NOT NULL REFERENCES employes(id),
    contrat_id      INTEGER REFERENCES contrat(id),
    date_depot      DATE        NOT NULL DEFAULT CURRENT_DATE,
    date_validite   DATE,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_onboarding_employe    ON onboarding(employe_id);
CREATE INDEX idx_onboarding_tache_ob   ON onboarding_tache(onboarding_id);
CREATE INDEX idx_document_employe      ON document(employe_id);
CREATE INDEX idx_document_contrat      ON document(contrat_id);

-- ============================================================
-- BLOC 5 : Journal d'audit
-- ============================================================

CREATE TABLE journal_audit (
    id                BIGSERIAL PRIMARY KEY,
    acteur_id         BIGINT      NOT NULL REFERENCES employes(id),
    type_action       VARCHAR(20) NOT NULL
        CHECK (type_action IN ('CONSULTATION','MODIFICATION','CREATION','SUPPRESSION','CONNEXION')),
    table_cible       VARCHAR(100) NOT NULL,
    enregistrement_id BIGINT,
    detail            TEXT,
    date_action       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_acteur  ON journal_audit(acteur_id);
CREATE INDEX idx_audit_table   ON journal_audit(table_cible);
CREATE INDEX idx_audit_date    ON journal_audit(date_action);

-- ============================================================
-- BLOC 6 : Temps de travail & Pointage
-- ============================================================

CREATE TABLE regime_travail (
    id                 BIGSERIAL PRIMARY KEY,
    libelle            VARCHAR(80) NOT NULL UNIQUE,
    nb_heures_semaine  INTEGER     NOT NULL CHECK (nb_heures_semaine > 0)
);

CREATE TABLE horaire (
    id              BIGSERIAL PRIMARY KEY,
    libelle         VARCHAR(80) NOT NULL UNIQUE,
    heure_debut     TIME        NOT NULL,
    heure_fin       TIME        NOT NULL,
    pause_minutes   INTEGER     DEFAULT 0 CHECK (pause_minutes >= 0),
    CONSTRAINT chk_horaire_ordre CHECK (heure_fin > heure_debut)
);

CREATE TABLE planning (
    id                BIGSERIAL PRIMARY KEY,
    employe_id        BIGINT  NOT NULL REFERENCES employes(id),
    horaire_id        BIGINT  NOT NULL REFERENCES horaire(id),
    semaine_debut     DATE    NOT NULL,  -- toujours un lundi
    regime_travail_id BIGINT REFERENCES regime_travail(id),
    CONSTRAINT uq_planning_employe_semaine UNIQUE (employe_id, semaine_debut)
);

CREATE TABLE pointage (
    id          BIGSERIAL PRIMARY KEY,
    employe_id  BIGINT      NOT NULL REFERENCES employes(id),
    direction   VARCHAR(10) NOT NULL CHECK (direction IN ('ENTREE', 'SORTIE')),
    horodatage  TIMESTAMP   NOT NULL,
    origine     VARCHAR(10) NOT NULL CHECK (origine IN ('BADGE', 'APPLI', 'MANUEL')),
    planning_id BIGINT REFERENCES planning(id),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE anomalie_pointage (
    id                       BIGSERIAL PRIMARY KEY,
    employe_id               BIGINT      NOT NULL REFERENCES employes(id),
    pointage_id              BIGINT REFERENCES pointage(id),
    type_anomalie            VARCHAR(40) NOT NULL
        CHECK (type_anomalie IN (
            'ENTREE_SANS_SORTIE','HORS_PLANNING','RETARD','HEURE_SUP_NON_JUSTIFIEE')),
    description              TEXT,
    statut                   VARCHAR(20) NOT NULL DEFAULT 'DETECTEE'
        CHECK (statut IN ('DETECTEE','JUSTIFIEE','IGNOREE')),
    commentaire_justification TEXT,
    detected_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_planning_employe   ON planning(employe_id);
CREATE INDEX idx_pointage_employe   ON pointage(employe_id);
CREATE INDEX idx_pointage_date      ON pointage(horodatage);
CREATE INDEX idx_anomalie_employe   ON anomalie_pointage(employe_id);

-- ============================================================
-- BLOC 7 : Congés & Absences
-- ============================================================

CREATE TABLE type_conge (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(20)  NOT NULL UNIQUE,
    libelle         VARCHAR(100) NOT NULL,
    plafonne        BOOLEAN      NOT NULL DEFAULT FALSE,
    duree_max_jours INTEGER CHECK (duree_max_jours IS NULL OR duree_max_jours > 0)
);

CREATE TABLE droit_conge (
    id              BIGSERIAL PRIMARY KEY,
    employe_id      BIGINT  NOT NULL REFERENCES employes(id),
    type_conge_id   BIGINT  NOT NULL REFERENCES type_conge(id),
    annee           INTEGER NOT NULL CHECK (annee >= 2000),
    jours_acquis    NUMERIC(5,1) NOT NULL CHECK (jours_acquis >= 0),
    CONSTRAINT uq_droit_conge UNIQUE (employe_id, type_conge_id, annee)
);

CREATE TABLE solde_conge (
    id              BIGSERIAL PRIMARY KEY,
    employe_id      BIGINT         NOT NULL REFERENCES employes(id),
    type_conge_id   BIGINT         NOT NULL REFERENCES type_conge(id),
    solde_courant   NUMERIC(5,1)   NOT NULL DEFAULT 0 CHECK (solde_courant >= 0),
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_solde_conge UNIQUE (employe_id, type_conge_id)
);

CREATE TABLE demande_conge (
    id                      BIGSERIAL PRIMARY KEY,
    employe_id              BIGINT      NOT NULL REFERENCES employes(id),
    type_conge_id           BIGINT      NOT NULL REFERENCES type_conge(id),
    date_debut              DATE        NOT NULL,
    date_fin                DATE        NOT NULL,
    statut                  VARCHAR(25) NOT NULL DEFAULT 'BROUILLON'
        CHECK (statut IN (
            'BROUILLON','SOUMISE','VALIDEE_MANAGER','VALIDEE_RH','REFUSEE','ANNULEE')),
    commentaire             TEXT,
    validateur_manager_id   BIGINT REFERENCES employes(id),
    date_validation_manager TIMESTAMP,
    validateur_rh_id        BIGINT REFERENCES employes(id),
    date_validation_rh      TIMESTAMP,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_demande_conge_dates CHECK (date_fin >= date_debut)
);

CREATE TABLE absence (
    id               BIGSERIAL PRIMARY KEY,
    employe_id       BIGINT      NOT NULL REFERENCES employes(id),
    type_absence     VARCHAR(20) NOT NULL
        CHECK (type_absence IN ('CONGE_ANNUEL','RTT','MALADIE','SANS_SOLDE','AUTRE')),
    date_debut       DATE        NOT NULL,
    date_fin         DATE        NOT NULL,
    demande_conge_id BIGINT REFERENCES demande_conge(id),
    justificatif_url VARCHAR(255),
    justifiee        BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_absence_dates CHECK (date_fin >= date_debut)
);

CREATE INDEX idx_demande_conge_employe ON demande_conge(employe_id);
CREATE INDEX idx_demande_conge_statut  ON demande_conge(statut);
CREATE INDEX idx_absence_employe       ON absence(employe_id);
CREATE INDEX idx_absence_dates         ON absence(date_debut, date_fin);
```

---

## 9. Repositories — interfaces à créer (toutes identiques en structure)

Modèle standard à répliquer pour chaque entité :

```java
package SI.RH.repository;

import SI.RH.entity.Onboarding;   // remplacer par l'entité concernée
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {
    List<Onboarding> findByEmployeId(Long employeId);
}
```

**Repositories à créer** (même pattern, queries spécifiques à adapter) :

| Interface | Méthode(s) utile(s) |
|---|---|
| `OnboardingRepository` | `findByEmployeId(Long)`, `findByStatut(StatutOnboarding)` |
| `OnboardingTacheRepository` | `findByOnboardingId(Long)`, `findByOnboardingIdAndObligatoireTrue(Long)` |
| `DocumentRepository` | `findByEmployeId(Long)`, `findByDateValiditeBefore(LocalDate)` |
| `JournalAuditRepository` | `findByActeurId(Long)`, `findByTableCibleAndEnregistrementId(String, Long)` |
| `RegimeTravailRepository` | *(CRUD standard)* |
| `HoraireRepository` | *(CRUD standard)* |
| `PlanningRepository` | `findByEmployeIdAndSemaineDebut(Long, LocalDate)` |
| `PointageRepository` | `findByEmployeIdAndHorodatageBetween(Long, LocalDateTime, LocalDateTime)` |
| `AnomaliePointageRepository` | `findByEmployeIdAndStatut(Long, StatutAnomalie)` |
| `TypeCongeRepository` | `findByCode(String)` |
| `DroitCongeRepository` | `findByEmployeIdAndTypeCongeIdAndAnnee(Long, Long, Integer)` |
| `SoldeCongeRepository` | `findByEmployeIdAndTypeCongeId(Long, Long)` |
| `DemandeCongeRepository` | `findByEmployeId(Long)`, `findByStatut(StatutDemandeConge)` |
| `AbsenceRepository` | `findByEmployeIdAndDateDebutBetween(Long, LocalDate, LocalDate)` |

---

## 10. Récapitulatif fichiers

```
src/main/java/SI/RH/
├── entity/
│   ├── Employe.java                  ← MODIFIER (ajouter departementId)
│   ├── Onboarding.java               ← CRÉER
│   ├── OnboardingTache.java          ← CRÉER
│   ├── Document.java                 ← CRÉER
│   ├── JournalAudit.java             ← CRÉER
│   ├── RegimeTravail.java            ← CRÉER
│   ├── Horaire.java                  ← CRÉER
│   ├── Planning.java                 ← CRÉER
│   ├── Pointage.java                 ← CRÉER
│   ├── AnomaliePointage.java         ← CRÉER
│   ├── TypeConge.java                ← CRÉER
│   ├── DroitConge.java               ← CRÉER
│   ├── SoldeConge.java               ← CRÉER
│   ├── DemandeConge.java             ← CRÉER
│   └── Absence.java                  ← CRÉER
│   └── enums/
│       ├── StatutOnboarding.java     ← CRÉER
│       ├── StatutTache.java          ← CRÉER
│       ├── TypeDocument.java         ← CRÉER
│       ├── TypeAudit.java            ← CRÉER
│       ├── DirectionPointage.java    ← CRÉER
│       ├── OriginePointage.java      ← CRÉER
│       ├── StatutAnomalie.java       ← CRÉER
│       ├── TypeAnomalie.java         ← CRÉER
│       ├── StatutDemandeConge.java   ← CRÉER
│       └── TypeAbsence.java          ← CRÉER
├── repository/
│   ├── OnboardingRepository.java     ← CRÉER
│   ├── OnboardingTacheRepository.java← CRÉER
│   ├── DocumentRepository.java       ← CRÉER
│   ├── JournalAuditRepository.java   ← CRÉER
│   ├── RegimeTravailRepository.java  ← CRÉER
│   ├── HoraireRepository.java        ← CRÉER
│   ├── PlanningRepository.java       ← CRÉER
│   ├── PointageRepository.java       ← CRÉER
│   ├── AnomaliePointageRepository.java← CRÉER
│   ├── TypeCongeRepository.java      ← CRÉER
│   ├── DroitCongeRepository.java     ← CRÉER
│   ├── SoldeCongeRepository.java     ← CRÉER
│   ├── DemandeCongeRepository.java   ← CRÉER
│   └── AbsenceRepository.java        ← CRÉER
└── service/
    └── ContratService.java           ← MODIFIER (déclencher onboarding à la signature)

sql/
└── base_complete.sql                 ← MODIFIER (ajouter blocs 4-7 avant COMMIT)
```
