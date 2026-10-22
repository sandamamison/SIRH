# Instructions & Plan de développement — Bloc 2 (Recrutement)

> **Destinataire :** Personne 2 (Bloc 2 : Offres d'emploi, Candidatures, Entretiens, Évaluations).  
> **Branche de travail conseillée :** `2-recrutement`  
> **Objectif :** Un backend MVC orienté service propre sous forme d'**API REST**, aligné sur les normes validées de l'équipe (`doc/normes.md`), calqué sur l'architecture du Bloc 1.

---

## 1. Rappel des normes communes (`doc/normes.md`)

| Règle | Application au Bloc 2 |
|---|---|
| **Base** | Nom unique : `sirh` (`jdbc:postgresql://localhost:5432/sirh`). |
| **Clé primaire (PK)** | Toujours **`id`** (`SERIAL PRIMARY KEY` / `Integer id`). Fini `id_offre`, `id_candidature`... |
| **Clé étrangère (FK)** | Toujours **`(table)Id`** en camelCase : `demandeRecrutementId`, `offreEmploiId`, `personneId`, `entretienId`, `employeEvaluateurId`, `employeActeurId`... |
| **SQL sans guillemets** | Ne jamais encadrer les noms de tables/colonnes de guillemets pour laisser PostgreSQL en minuscules. |
| **Enums en base** | Stocker en `VARCHAR` avec contrainte `CHECK (...)` (les types ENUM partagés PostgreSQL posent des conflits de merge et des bugs Hibernate). |
| **Architecture Java** | `Controller (REST) → Service (@Transactional, règles RG2) → Repository (Spring Data) → Entity (JPA)` |
| **Types temporels** | Exclusivement `LocalDate` et `LocalDateTime` (jamais `java.util.Date`). |
| **Configuration** | Ne pas commiter `application.properties` (utiliser `application.properties.example`). |

---

## 2. Script SQL autonome : `sirh_conception/bloc2.sql`

Ce script est à exécuter dans la base `sirh` après `bloc1.sql`. Les références aux tables du Bloc 3 (`personne`, `employe`) sont gardées sous forme de colonnes `INTEGER` simples ; les contraintes `REFERENCES` seront ajoutées lors de la fusion du mercredi.

```sql
-- ============================================================================
-- SIRH - BLOC 2 : Recrutement
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- Enums   : VARCHAR + CHECK
-- ============================================================================

CREATE TABLE offreEmploi (
    id                    SERIAL PRIMARY KEY,
    demandeRecrutementId  INTEGER UNIQUE NOT NULL REFERENCES demandeRecrutement(id), -- 1:1 RG2-01
    titreOffre            VARCHAR(150) NOT NULL,
    description           TEXT NOT NULL,
    salairePropose        INTEGER CHECK (salairePropose IS NULL OR salairePropose > 0),
    horairesTravail       VARCHAR(100),
    lieuTravail           VARCHAR(150) NOT NULL,
    datePublication       DATE,
    dateCloture           DATE,
    statut                VARCHAR(20) NOT NULL DEFAULT 'BROUILLON'
        CHECK (statut IN ('BROUILLON', 'PUBLIEE', 'CLOTUREE', 'ANNULEE')),
    CONSTRAINT chk_dates_offre
        CHECK (dateCloture IS NULL OR datePublication IS NULL OR dateCloture >= datePublication),
    CONSTRAINT chk_offre_publiee
        CHECK (statut <> 'PUBLIEE' OR datePublication IS NOT NULL),
    CONSTRAINT chk_offre_cloturee
        CHECK (statut <> 'CLOTUREE' OR dateCloture IS NOT NULL)
);

CREATE TABLE candidature (
    id                  SERIAL PRIMARY KEY,
    offreEmploiId       INTEGER NOT NULL REFERENCES offreEmploi(id),
    personneId          INTEGER NOT NULL,                         -- FK vers personne (Bloc 3)
    dateCandidature     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cvUrl               VARCHAR(255),
    lettreMotivation    TEXT,
    pretentionSalariale INTEGER CHECK (pretentionSalariale IS NULL OR pretentionSalariale > 0),
    accordSalaire       BOOLEAN,
    remarqueSalaire     TEXT,
    statut              VARCHAR(30) NOT NULL DEFAULT 'RECUE'
        CHECK (statut IN ('RECUE', 'PRESELECTIONNEE', 'EN_ENTRETIEN', 'RETENUE', 'REJETEE')),
    -- RG2-04 : un candidat ne postule qu'une seule fois a la meme offre
    CONSTRAINT uq_candidat_offre UNIQUE (offreEmploiId, personneId)
);

CREATE TABLE historiqueStatutCandidature (
    id                  SERIAL PRIMARY KEY,
    candidatureId       INTEGER NOT NULL REFERENCES candidature(id),
    ancienStatut        VARCHAR(30),
    nouveauStatut       VARCHAR(30) NOT NULL,
    employeActeurId     INTEGER NOT NULL,                         -- FK vers employe (RH acteur)
    commentaire         TEXT,
    dateTransition      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- RG2-07
);

CREATE TABLE entretien (
    id              SERIAL PRIMARY KEY,
    candidatureId   INTEGER NOT NULL REFERENCES candidature(id),
    dateHeure       TIMESTAMP NOT NULL,
    lieuOuLien      VARCHAR(255),
    typeEntretien   VARCHAR(30) NOT NULL DEFAULT 'RH'
        CHECK (typeEntretien IN ('RH', 'TECHNIQUE', 'DIRECTION', 'TEST')),
    statut          VARCHAR(20) NOT NULL DEFAULT 'PLANIFIE'
        CHECK (statut IN ('PLANIFIE', 'EFFECTUE', 'ANNULE', 'REPORTE'))
);

CREATE TABLE intervenantEntretien (
    id              SERIAL PRIMARY KEY,
    entretienId     INTEGER NOT NULL REFERENCES entretien(id) ON DELETE CASCADE,
    employeId       INTEGER NOT NULL,                             -- FK vers employe (jury)
    roleIntervenant VARCHAR(50) DEFAULT 'EXAMINATEUR',
    CONSTRAINT uq_entretien_intervenant UNIQUE (entretienId, employeId)
);

CREATE TABLE evaluationCandidat (
    id                  SERIAL PRIMARY KEY,
    entretienId         INTEGER NOT NULL REFERENCES entretien(id),
    employeEvaluateurId INTEGER NOT NULL,                         -- FK vers employe (evaluateur)
    scoreGlobal         INTEGER CHECK (scoreGlobal IS NULL OR (scoreGlobal >= 0 AND scoreGlobal <= 20)),
    pointsForts         TEXT,
    pointsFaibles       TEXT,
    avis                VARCHAR(20) NOT NULL
        CHECK (avis IN ('FAVORABLE', 'RESERVE', 'DEFAVORABLE')),
    dateEvaluation      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- RG2-06 : une seule evaluation par evaluateur pour un entretien donne
    CONSTRAINT uq_entretien_evaluateur UNIQUE (entretienId, employeEvaluateurId)
);

-- Index sur les FK
CREATE INDEX idx_offre_demande          ON offreEmploi(demandeRecrutementId);
CREATE INDEX idx_candidature_offre      ON candidature(offreEmploiId);
CREATE INDEX idx_candidature_personne   ON candidature(personneId);
CREATE INDEX idx_historique_cand        ON historiqueStatutCandidature(candidatureId);
CREATE INDEX idx_entretien_candidature  ON entretien(candidatureId);
CREATE INDEX idx_eval_entretien         ON evaluationCandidat(entretienId);
```

---

## 3. Énumérations Java à créer

Emplacement : `src/main/java/SI/RH/entity/enums/`

1. **`StatutOffre.java`** :
   ```java
   public enum StatutOffre {
       BROUILLON, PUBLIEE, CLOTUREE, ANNULEE;

       public boolean peutPasserA(StatutOffre cible) {
           return switch (this) {
               case BROUILLON -> cible == PUBLIEE || cible == ANNULEE;
               case PUBLIEE   -> cible == CLOTUREE || cible == ANNULEE;
               case CLOTUREE, ANNULEE -> false;
           };
       }
   }
   ```
2. **`StatutCandidature.java`** :
   ```java
   public enum StatutCandidature {
       RECUE, PRESELECTIONNEE, EN_ENTRETIEN, RETENUE, REJETEE;

       public boolean peutPasserA(StatutCandidature cible) {
           return switch (this) {
               case RECUE           -> cible == PRESELECTIONNEE || cible == REJETEE;
               case PRESELECTIONNEE -> cible == EN_ENTRETIEN || cible == REJETEE;
               case EN_ENTRETIEN    -> cible == RETENUE || cible == REJETEE;
               case RETENUE, REJETEE -> false;
           };
       }
   }
   ```
3. **`TypeEntretien.java`** : `RH, TECHNIQUE, DIRECTION, TEST`
4. **`StatutEntretien.java`** : `PLANIFIE, EFFECTUE, ANNULE, REPORTE`
5. **`AvisEvaluation.java`** : `FAVORABLE, RESERVE, DEFAVORABLE`

---

## 4. Entités JPA à créer

Emplacement : `src/main/java/SI/RH/entity/`  
*Modèle à suivre : voir `DemandeRecrutement.java` et `Departement.java`.*

* **`OffreEmploi.java`** :
  * `@Entity @Table(name = "offreEmploi")`
  * `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;`
  * `@Column(nullable = false, unique = true) private Integer demandeRecrutementId;`
  * `@Column(nullable = false, length = 150) private String titreOffre;`
  * `@Column(nullable = false, columnDefinition = "TEXT") private String description;`
  * `private Integer salairePropose;`
  * `private String horairesTravail;`
  * `@Column(nullable = false, length = 150) private String lieuTravail;`
  * `private LocalDate datePublication;`
  * `private LocalDate dateCloture;`
  * `@Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private StatutOffre statut = StatutOffre.BROUILLON;`

* **`Candidature.java`** :
  * `@Entity @Table(name = "candidature")`
  * `@Id @GeneratedValue... private Integer id;`
  * `@Column(nullable = false) private Integer offreEmploiId;`
  * `@Column(nullable = false) private Integer personneId;`
  * `@Column(nullable = false, updatable = false) private LocalDateTime dateCandidature;`
  * `private String cvUrl;`
  * `@Column(columnDefinition = "TEXT") private String lettreMotivation;`
  * `private Integer pretentionSalariale;`
  * `private Boolean accordSalaire;`
  * `private String remarqueSalaire;`
  * `@Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private StatutCandidature statut = StatutCandidature.RECUE;`
  * `@PrePersist` pour initialiser `dateCandidature`.

* **`HistoriqueStatutCandidature.java`** :
  * `@Entity @Table(name = "historiqueStatutCandidature")`
  * `id`, `candidatureId`, `ancienStatut`, `nouveauStatut`, `employeActeurId`, `commentaire`, `dateTransition` (`@PrePersist`).

* **`Entretien.java`** :
  * `@Entity @Table(name = "entretien")`
  * `id`, `candidatureId`, `dateHeure` (`LocalDateTime`), `lieuOuLien`, `@Enumerated TypeEntretien typeEntretien`, `@Enumerated StatutEntretien statut`.

* **`IntervenantEntretien.java`** :
  * `@Entity @Table(name = "intervenantEntretien")`
  * `id`, `entretienId`, `employeId`, `roleIntervenant`.

* **`EvaluationCandidat.java`** :
  * `@Entity @Table(name = "evaluationCandidat")`
  * `id`, `entretienId`, `employeEvaluateurId`, `scoreGlobal` (0..20), `pointsForts`, `pointsFaibles`, `@Enumerated AvisEvaluation avis`, `dateEvaluation` (`@PrePersist`).

---

## 5. Règles de gestion à coder dans les Services

### Lien critique avec le Bloc 1 (Contrat D8)
* **RG2-01** : Lorsqu'une offre d'emploi est créée ou publiée à partir d'une demande de recrutement :
  1. Vérifier que la `demandeRecrutement` existe et a le statut `VALIDEE_DIRECTION`.
  2. Vérifier qu'aucune offre n'a déjà été créée pour cette demande (`offreEmploiRepository.existsByDemandeRecrutementId(...)`).
  3. Appeler la méthode du service Bloc 1 :
     ```java
     demandeRecrutementService.marquerRecrutementOuvert(demandeId, acteurId);
     ```
     *(Cette méthode existe déjà dans `SI.RH.service.DemandeRecrutementService` et passe la demande à `RECRUTEMENT_OUVERT`).*

### Autres règles de gestion
* **RG2-02** : Publication et clôture d'offre avec vérification des dates (`datePublication` renseignée à la publication, `dateCloture >= datePublication`).
* **RG2-04** : Un candidat ne peut postuler qu'une seule fois à la même offre (`candidatureRepository.existsByOffreEmploiIdAndPersonneId(...)`).
* **RG2-06** : La note d'évaluation `scoreGlobal` doit être comprise entre 0 et 20. Un évaluateur ne peut déposer qu'une évaluation par entretien.
* **RG2-07** : Tout changement de statut d'une candidature (`RECUE` → `PRESELECTIONNEE` → `EN_ENTRETIEN` → `RETENUE` / `REJETEE`) doit obligatoirement écrire une ligne dans `historiqueStatutCandidature`.
* **RG2-08** : Clôture automatique de l'offre quand le nombre de candidatures au statut `RETENUE` atteint l'effectif demandé sur la demande de recrutement initiale (`demande.getEffectifDemande()`).

---

## 6. Architecture des composants à livrer

1. **Repositories** (`SI.RH.repository`) :
   * `OffreEmploiRepository`, `CandidatureRepository`, `HistoriqueStatutCandidatureRepository`, `EntretienRepository`, `EvaluationCandidatRepository`.
2. **DTO Records** (`SI.RH.dto`) :
   * `OffreEmploiRequest`, `OffreEmploiResponse`, `CandidatureRequest`, `CandidatureResponse`, `EntretienRequest`, `EntretienResponse`, `EvaluationRequest`, `EvaluationResponse`.
3. **Services** (`SI.RH.service`) :
   * `OffreEmploiService` (création, publication, clôture)
   * `CandidatureService` (dépôt, changement de statut, traçabilité)
   * `EntretienService` (planification, ajout d'évaluation)
4. **Contrôleurs REST** (`SI.RH.controller`) :
   * `/api/offres` (`POST`, `GET`, `PUT /{id}/publier`, `PUT /{id}/cloturer`)
   * `/api/candidatures` (`POST`, `GET ?offreId=`, `PUT /{id}/statut`)
   * `/api/entretiens` (`POST`, `GET ?candidatureId=`, `POST /{id}/evaluations`)
5. **Tests** (`src/test/java/SI/RH/`) :
   * Tests unitaires sur les règles RG2-01, RG2-04, RG2-06, RG2-08.
