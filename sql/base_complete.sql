-- ============================================================================
-- SIRH - Reinitialisation complete du schema applicatif
-- PostgreSQL - SCRIPT DESTRUCTIF : les donnees des tables ci-dessous seront
-- supprimees avant la recreation.
--
-- Perimetre : entites Java actuelles du projet.
-- Les tables non listees ici ne sont pas modifiees.
-- ============================================================================


BEGIN;

CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Suppression des objets du schema applicatif.
DROP FUNCTION IF EXISTS mettre_a_jour_updated_at() CASCADE;

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
    onboarding,
    evaluationcandidat,
    intervenantentretien,
    historiquestatutcandidature,
    historique_propositions,
    historiqueStatutDemande,
    validationDemande,
    affectation,
    contrat,
    utilisateur_roles,
    utilisateurs,
    entretien,
    propositions_embauche,
    candidature,
    offreEmploi,
    demandeRecrutement,
    poste,
    departement,
    employes,
    personnes
    CASCADE;

-- Personnes et salaries.
CREATE TABLE personnes
(
    id         BIGSERIAL PRIMARY KEY,
    nom        VARCHAR(100) NOT NULL,
    prenom     VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    telephone  VARCHAR(30),
    adresse    VARCHAR(255),
    createdAt  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employes
(
    id           BIGSERIAL PRIMARY KEY,
    matricule    VARCHAR(30) NOT NULL UNIQUE,
    personneid   BIGINT      NOT NULL UNIQUE REFERENCES personnes (id),
    dateembauche DATE        NOT NULL,
    statut       VARCHAR(50) NOT NULL DEFAULT 'ACTIF',
    createdAt    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_employes_statut
        CHECK (statut IN ('ACTIF', 'INACTIF'))
);

CREATE TABLE utilisateurs
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR(80)  NOT NULL UNIQUE,
    passwordhash VARCHAR(255) NOT NULL,
    personneid   BIGINT       NOT NULL UNIQUE REFERENCES personnes (id) ON DELETE CASCADE,
    actif        BOOLEAN      NOT NULL DEFAULT TRUE,
    createdat    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE utilisateur_roles
(
    utilisateurid BIGINT PRIMARY KEY REFERENCES utilisateurs (id) ON DELETE CASCADE,
    role          VARCHAR(20) NOT NULL
        CHECK (role IN ('CANDIDAT', 'SALARIE', 'MANAGER', 'RH', 'DIRECTION', 'ADMIN'))
);

-- Organisation.
CREATE TABLE departement
(
    id                   SERIAL PRIMARY KEY,
    codeDepartement      VARCHAR(20)  NOT NULL UNIQUE,
    nomDepartement       VARCHAR(100) NOT NULL,
    description          TEXT,
    employeResponsableId BIGINT REFERENCES employes (id),
    departementParentId  INTEGER REFERENCES departement (id),
    CONSTRAINT chk_departement_parent_diff
        CHECK (departementParentId IS NULL OR departementParentId <> id)
);

-- Rattachement organisationnel direct de l'employe (Bloc 5)
ALTER TABLE employes ADD COLUMN IF NOT EXISTS departementid BIGINT REFERENCES departement(id);

CREATE TABLE poste
(
    id                 SERIAL PRIMARY KEY,
    codePoste          VARCHAR(30)  NOT NULL UNIQUE,
    titre              VARCHAR(100) NOT NULL,
    descriptionMission TEXT,
    salaireMin         INTEGER CHECK (salaireMin IS NULL OR salaireMin >= 0),
    salaireMax         INTEGER CHECK (salaireMax IS NULL OR salaireMax >= 0),
    departementId      INTEGER      NOT NULL REFERENCES departement (id),
    CONSTRAINT chk_poste_salaires
        CHECK (salaireMin IS NULL OR salaireMax IS NULL OR salaireMax >= salaireMin)
);

-- Besoins et validations de recrutement.
CREATE TABLE demandeRecrutement
(
    id                 SERIAL PRIMARY KEY,
    posteId            INTEGER     NOT NULL REFERENCES poste (id),
    motif              VARCHAR(20) NOT NULL
        CHECK (motif IN ('CREATION', 'REMPLACEMENT')),
    justification      TEXT        NOT NULL,
    employeRemplaceId  BIGINT REFERENCES employes (id),
    effectifDemande    INTEGER     NOT NULL DEFAULT 1 CHECK (effectifDemande > 0),
    budgetPrevu        BIGINT      NOT NULL CHECK (budgetPrevu > 0),
    dateSouhaitee      DATE        NOT NULL,
    statut             VARCHAR(30) NOT NULL DEFAULT 'BROUILLON'
        CHECK (statut IN (
                          'BROUILLON', 'SOUMISE', 'VALIDEE_RH', 'VALIDEE_DIRECTION',
                          'RECRUTEMENT_OUVERT', 'CLOTUREE', 'REJETEE'
            )),
    employeDemandeurId BIGINT      NOT NULL REFERENCES employes (id),
    dateCreation       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_demande_remplacement
        CHECK ((motif = 'REMPLACEMENT') = (employeRemplaceId IS NOT NULL))
);

CREATE TABLE validationDemande
(
    id                   SERIAL PRIMARY KEY,
    demandeRecrutementId INTEGER     NOT NULL REFERENCES demandeRecrutement (id),
    employeValideurId    BIGINT      NOT NULL REFERENCES employes (id),
    niveau               VARCHAR(20) NOT NULL CHECK (niveau IN ('RH', 'DIRECTION')),
    decision             VARCHAR(20) NOT NULL CHECK (decision IN ('APPROUVE', 'REJETE')),
    commentaire          TEXT,
    dateDecision         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_validation_demande_niveau
        UNIQUE (demandeRecrutementId, niveau)
);

CREATE TABLE historiqueStatutDemande
(
    id                   SERIAL PRIMARY KEY,
    demandeRecrutementId INTEGER     NOT NULL REFERENCES demandeRecrutement (id),
    ancienStatut         VARCHAR(30),
    nouveauStatut        VARCHAR(30) NOT NULL,
    employeActeurId      BIGINT      NOT NULL REFERENCES employes (id),
    commentaire          TEXT,
    dateTransition       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Offres, candidatures et entretiens.
CREATE TABLE offreEmploi
(
    id                   SERIAL PRIMARY KEY,
    demandeRecrutementId INTEGER      NOT NULL UNIQUE REFERENCES demandeRecrutement (id),
    titreOffre           VARCHAR(150) NOT NULL,
    description          TEXT         NOT NULL,
    salairePropose       INTEGER CHECK (salairePropose IS NULL OR salairePropose > 0),
    horairesTravail      VARCHAR(100),
    lieuTravail          VARCHAR(150) NOT NULL,
    datePublication      DATE,
    dateCloture          DATE,
    statut               VARCHAR(20)  NOT NULL DEFAULT 'BROUILLON'
        CHECK (statut IN ('BROUILLON', 'PUBLIEE', 'CLOTUREE', 'ANNULEE')),
    CONSTRAINT chk_offre_dates
        CHECK (dateCloture IS NULL OR datePublication IS NULL OR dateCloture >= datePublication),
    CONSTRAINT chk_offre_publiee
        CHECK (statut <> 'PUBLIEE' OR datePublication IS NOT NULL),
    CONSTRAINT chk_offre_cloturee
        CHECK (statut <> 'CLOTUREE' OR dateCloture IS NOT NULL)
);

CREATE TABLE candidature
(
    id                  BIGSERIAL PRIMARY KEY,
    offreemploiid       BIGINT      NOT NULL REFERENCES offreEmploi (id),
    personneid          BIGINT      NOT NULL REFERENCES personnes (id),
    datecandidature     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cvurl               VARCHAR(255),
    lettremotivation    TEXT,
    pretentionsalariale INTEGER CHECK (pretentionsalariale IS NULL OR pretentionsalariale > 0),
    accordsalaire       BOOLEAN,
    remarquesalaire     TEXT,
    statut              VARCHAR(30) NOT NULL DEFAULT 'RECUE'
        CHECK (statut IN ('RECUE', 'PRESELECTIONNEE', 'EN_ENTRETIEN', 'RETENUE', 'REJETEE')),
    CONSTRAINT candidature_offre_personne_unique UNIQUE (offreemploiid, personneid)
);

CREATE TABLE historiquestatutcandidature
(
    id              BIGSERIAL PRIMARY KEY,
    candidatureid   BIGINT      NOT NULL REFERENCES candidature (id) ON DELETE CASCADE,
    ancienstatut    VARCHAR(30),
    nouveauStatut   VARCHAR(30) NOT NULL
        CHECK (nouveauStatut IN ('RECUE', 'PRESELECTIONNEE', 'EN_ENTRETIEN', 'RETENUE', 'REJETEE')),
    employeacteurid BIGINT      NOT NULL REFERENCES employes (id),
    commentaire     TEXT,
    datetransition  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE entretien
(
    id            BIGSERIAL PRIMARY KEY,
    candidatureid BIGINT      NOT NULL REFERENCES candidature (id) ON DELETE CASCADE,
    dateheure     TIMESTAMP   NOT NULL,
    lieuoulien    VARCHAR(255),
    typeentretien VARCHAR(30) NOT NULL DEFAULT 'RH'
        CHECK (typeentretien IN ('RH', 'TECHNIQUE', 'DIRECTION', 'TEST')),
    statut        VARCHAR(20) NOT NULL DEFAULT 'PLANIFIE'
        CHECK (statut IN ('PLANIFIE', 'EFFECTUE', 'ANNULE', 'REPORTE'))
);

CREATE TABLE intervenantentretien
(
    id              BIGSERIAL PRIMARY KEY,
    entretienid     BIGINT      NOT NULL REFERENCES entretien (id) ON DELETE CASCADE,
    employeid       BIGINT      NOT NULL REFERENCES employes (id),
    roleintervenant VARCHAR(50) NOT NULL DEFAULT 'EXAMINATEUR',
    CONSTRAINT intervenant_entretien_unique UNIQUE (entretienid, employeid)
);

CREATE TABLE evaluationcandidat
(
    id                  BIGSERIAL PRIMARY KEY,
    entretienid         BIGINT      NOT NULL REFERENCES entretien (id) ON DELETE CASCADE,
    employeevaluateurid BIGINT      NOT NULL REFERENCES employes (id),
    scoreglobal         INTEGER CHECK (scoreglobal BETWEEN 0 AND 20),
    pointsforts         TEXT,
    pointsfaibles       TEXT,
    avis                VARCHAR(20) NOT NULL
        CHECK (avis IN ('FAVORABLE', 'RESERVE', 'DEFAVORABLE')),
    dateevaluation      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT evaluation_entretien_evaluateur_unique
        UNIQUE (entretienid, employeevaluateurid)
);

-- Proposition d'embauche et historique.
CREATE TABLE propositions_embauche
(
    id              BIGSERIAL PRIMARY KEY,
    candidature_id  BIGINT      NOT NULL UNIQUE REFERENCES candidature (id) ON DELETE CASCADE,
    statut          VARCHAR(20) NOT NULL DEFAULT 'ENVOYEE'
        CHECK (statut IN ('ENVOYEE', 'ACCEPTEE', 'REFUSEE', 'EXPIREE')),
    date_envoi      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_reponse    TIMESTAMP,
    date_expiration DATE,
    commentaire     TEXT,
    created_by      VARCHAR(100),
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_proposition_reponse
        CHECK (statut NOT IN ('ACCEPTEE', 'REFUSEE') OR date_reponse IS NOT NULL),
    CONSTRAINT chk_proposition_dates
        CHECK (date_reponse IS NULL OR date_reponse >= date_envoi)
);

CREATE TABLE historique_propositions
(
    id              BIGSERIAL PRIMARY KEY,
    proposition_id  BIGINT      NOT NULL REFERENCES propositions_embauche (id) ON DELETE CASCADE,
    ancien_statut   VARCHAR(20)
        CHECK (ancien_statut IS NULL OR ancien_statut IN ('ENVOYEE', 'ACCEPTEE', 'REFUSEE', 'EXPIREE')),
    nouveau_statut  VARCHAR(20) NOT NULL
        CHECK (nouveau_statut IN ('ENVOYEE', 'ACCEPTEE', 'REFUSEE', 'EXPIREE')),
    date_transition TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    auteur          VARCHAR(100),
    commentaire     TEXT
);

-- Contrats et affectations.
CREATE TABLE contrat
(
    id                  SERIAL PRIMARY KEY,
    numeroContrat       VARCHAR(50) NOT NULL UNIQUE,
    employeId           BIGINT      NOT NULL REFERENCES employes (id),
    typeContrat         VARCHAR(10) NOT NULL DEFAULT 'CE'
        CHECK (typeContrat IN ('CE', 'CDD', 'CDI')),
    dateDebut           DATE        NOT NULL,
    dateFin             DATE,
    salaireBrut         INTEGER     NOT NULL CHECK (salaireBrut > 0),
    statut              VARCHAR(20) NOT NULL DEFAULT 'CREE'
        CHECK (statut IN ('CREE', 'SIGNE', 'RENOUVELE', 'NON_RENOUVELE', 'TERMINE', 'ROMPU')),
    contratPrecedentId  INTEGER,
    employeCreateurId   BIGINT      NOT NULL REFERENCES employes (id),
    employeSignataireId BIGINT REFERENCES employes (id),
    dateSignature       DATE,
    dateCreation        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_contrat_fin_selon_type
        CHECK ((typeContrat = 'CDI') = (dateFin IS NULL)),
    CONSTRAINT chk_dates_contrat
        CHECK (dateFin IS NULL OR dateFin >= dateDebut),
    CONSTRAINT chk_contrat_signature
        CHECK (
            (statut IN ('SIGNE', 'RENOUVELE', 'NON_RENOUVELE', 'TERMINE', 'ROMPU'))
                = (dateSignature IS NOT NULL AND employeSignataireId IS NOT NULL)
            ),
    CONSTRAINT chk_contrat_precedent_diff
        CHECK (contratPrecedentId IS NULL OR contratPrecedentId <> id),
    CONSTRAINT uq_contrat_id_employe UNIQUE (id, employeId),
    CONSTRAINT uq_contrat_successeur UNIQUE (contratPrecedentId),
    CONSTRAINT fk_contrat_precedent_meme_employe
        FOREIGN KEY (contratPrecedentId, employeId) REFERENCES contrat (id, employeId)
);

ALTER TABLE contrat
    ADD CONSTRAINT ex_contrat_sans_chevauchement
        EXCLUDE USING gist (
        employeId WITH =,
        daterange(dateDebut, dateFin, '[]') WITH &&
        );

CREATE TABLE affectation
(
    id        SERIAL PRIMARY KEY,
    employeId BIGINT  NOT NULL REFERENCES employes (id),
    posteId   INTEGER NOT NULL REFERENCES poste (id),
    dateDebut DATE    NOT NULL,
    dateFin   DATE,
    CONSTRAINT chk_dates_affectation
        CHECK (dateFin IS NULL OR dateFin >= dateDebut)
);

ALTER TABLE affectation
    ADD CONSTRAINT ex_affectation_sans_chevauchement
        EXCLUDE USING gist (
        employeId WITH =,
        daterange(dateDebut, dateFin, '[]') WITH &&
        );

-- Index des clés étrangères et des recherches fréquentes.
CREATE INDEX idx_poste_departement ON poste (departementId);
CREATE INDEX idx_demande_poste ON demandeRecrutement (posteId);
CREATE INDEX idx_demande_statut ON demandeRecrutement (statut);
CREATE INDEX idx_demande_demandeur ON demandeRecrutement (employeDemandeurId);
CREATE INDEX idx_validation_demande ON validationDemande (demandeRecrutementId);
CREATE INDEX idx_historique_demande ON historiqueStatutDemande (demandeRecrutementId);
CREATE INDEX idx_candidature_offre ON candidature (offreemploiid);
CREATE INDEX idx_candidature_personne ON candidature (personneid);
CREATE INDEX idx_historique_candidature ON historiquestatutcandidature (candidatureid);
CREATE INDEX idx_entretien_candidature ON entretien (candidatureid);
CREATE INDEX idx_intervenant_entretien ON intervenantentretien (entretienid);
CREATE INDEX idx_intervenant_employe ON intervenantentretien (employeid);
CREATE INDEX idx_evaluation_entretien ON evaluationcandidat (entretienid);
CREATE INDEX idx_evaluation_evaluateur ON evaluationcandidat (employeevaluateurid);
CREATE INDEX idx_contrat_employe ON contrat (employeId);
CREATE INDEX idx_contrat_precedent ON contrat (contratPrecedentId);
CREATE INDEX idx_affectation_employe ON affectation (employeId);
CREATE INDEX idx_affectation_poste ON affectation (posteId);

-- Onboarding et documents (Bloc 4)
CREATE TABLE onboarding (
    id          BIGSERIAL PRIMARY KEY,
    employeId   BIGINT      NOT NULL REFERENCES employes(id),
    dateDebut   DATE        NOT NULL,
    statut      VARCHAR(20) NOT NULL DEFAULT 'EN_COURS'
        CHECK (statut IN ('EN_COURS', 'TERMINE', 'ANNULE')),
    createdAt   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE onboarding_tache (
    id              BIGSERIAL PRIMARY KEY,
    onboardingId    BIGINT      NOT NULL REFERENCES onboarding(id) ON DELETE CASCADE,
    libelle         VARCHAR(150) NOT NULL,
    responsableId   BIGINT REFERENCES employes(id),
    echeance        DATE,
    statut          VARCHAR(20) NOT NULL DEFAULT 'A_FAIRE'
        CHECK (statut IN ('A_FAIRE', 'EN_COURS', 'FAIT')),
    obligatoire     BOOLEAN     NOT NULL DEFAULT TRUE,
    createdAt       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE document (
    id              BIGSERIAL PRIMARY KEY,
    typeDocument    VARCHAR(30) NOT NULL
        CHECK (typeDocument IN ('CONTRAT_SIGNE','PIECE_IDENTITE','DIPLOME','JUSTIFICATIF','AUTRE')),
    fichierUrl      VARCHAR(255) NOT NULL,
    employeId       BIGINT      NOT NULL REFERENCES employes(id),
    contratId       INTEGER REFERENCES contrat(id),
    dateDepot       DATE        NOT NULL DEFAULT CURRENT_DATE,
    dateValidite    DATE,
    createdAt       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_onboarding_employe    ON onboarding(employeId);
CREATE INDEX idx_onboarding_tache_ob   ON onboarding_tache(onboardingId);
CREATE INDEX idx_document_employe      ON document(employeId);
CREATE INDEX idx_document_contrat      ON document(contratId);

-- Journal d'audit (Bloc 5)
CREATE TABLE journal_audit (
    id                BIGSERIAL PRIMARY KEY,
    acteurId          BIGINT      NOT NULL REFERENCES employes(id),
    typeAction        VARCHAR(20) NOT NULL
        CHECK (typeAction IN ('CONSULTATION','MODIFICATION','CREATION','SUPPRESSION','CONNEXION')),
    tableCible        VARCHAR(100) NOT NULL,
    enregistrementId  BIGINT,
    detail            TEXT,
    dateAction        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_acteur  ON journal_audit(acteurId);
CREATE INDEX idx_audit_table   ON journal_audit(tableCible);
CREATE INDEX idx_audit_date    ON journal_audit(dateAction);

-- Temps de travail et pointage (Bloc 6)
CREATE TABLE regime_travail (
    id                 BIGSERIAL PRIMARY KEY,
    libelle            VARCHAR(80) NOT NULL UNIQUE,
    nbHeuresSemaine    INTEGER     NOT NULL CHECK (nbHeuresSemaine > 0)
);

CREATE TABLE horaire (
    id              BIGSERIAL PRIMARY KEY,
    libelle         VARCHAR(80) NOT NULL UNIQUE,
    heureDebut      TIME        NOT NULL,
    heureFin        TIME        NOT NULL,
    pauseMinutes    INTEGER     DEFAULT 0 CHECK (pauseMinutes >= 0),
    CONSTRAINT chk_horaire_ordre CHECK (heureFin > heureDebut)
);

CREATE TABLE planning (
    id                BIGSERIAL PRIMARY KEY,
    employeId         BIGINT  NOT NULL REFERENCES employes(id),
    horaireId         BIGINT  NOT NULL REFERENCES horaire(id),
    semaineDebut      DATE    NOT NULL,
    regimeTravailId   BIGINT REFERENCES regime_travail(id),
    CONSTRAINT uq_planning_employe_semaine UNIQUE (employeId, semaineDebut)
);

CREATE TABLE pointage (
    id          BIGSERIAL PRIMARY KEY,
    employeId   BIGINT      NOT NULL REFERENCES employes(id),
    direction   VARCHAR(10) NOT NULL CHECK (direction IN ('ENTREE', 'SORTIE')),
    horodatage  TIMESTAMP   NOT NULL,
    origine     VARCHAR(10) NOT NULL CHECK (origine IN ('BADGE', 'APPLI', 'MANUEL')),
    planningId  BIGINT REFERENCES planning(id),
    createdAt   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE anomalie_pointage (
    id                       BIGSERIAL PRIMARY KEY,
    employeId                BIGINT      NOT NULL REFERENCES employes(id),
    pointageId               BIGINT REFERENCES pointage(id),
    typeAnomalie             VARCHAR(40) NOT NULL
        CHECK (typeAnomalie IN (
            'ENTREE_SANS_SORTIE','HORS_PLANNING','RETARD','HEURE_SUP_NON_JUSTIFIEE')),
    description              TEXT,
    statut                   VARCHAR(20) NOT NULL DEFAULT 'DETECTEE'
        CHECK (statut IN ('DETECTEE','JUSTIFIEE','IGNOREE')),
    commentaireJustification TEXT,
    detectedAt               TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_planning_employe   ON planning(employeId);
CREATE INDEX idx_pointage_employe   ON pointage(employeId);
CREATE INDEX idx_pointage_date      ON pointage(horodatage);
CREATE INDEX idx_anomalie_employe   ON anomalie_pointage(employeId);

-- Congés et absences (Bloc 7)
CREATE TABLE type_conge (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(20)  NOT NULL UNIQUE,
    libelle         VARCHAR(100) NOT NULL,
    plafonne        BOOLEAN      NOT NULL DEFAULT FALSE,
    dureeMaxJours   INTEGER CHECK (dureeMaxJours IS NULL OR dureeMaxJours > 0)
);

CREATE TABLE droit_conge (
    id              BIGSERIAL PRIMARY KEY,
    employeId       BIGINT  NOT NULL REFERENCES employes(id),
    typeCongeId     BIGINT  NOT NULL REFERENCES type_conge(id),
    annee           INTEGER NOT NULL CHECK (annee >= 2000),
    joursAcquis     NUMERIC(5,1) NOT NULL CHECK (joursAcquis >= 0),
    CONSTRAINT uq_droit_conge UNIQUE (employeId, typeCongeId, annee)
);

CREATE TABLE solde_conge (
    id              BIGSERIAL PRIMARY KEY,
    employeId       BIGINT         NOT NULL REFERENCES employes(id),
    typeCongeId     BIGINT         NOT NULL REFERENCES type_conge(id),
    soldeCourant    NUMERIC(5,1)   NOT NULL DEFAULT 0 CHECK (soldeCourant >= 0),
    updatedAt       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_solde_conge UNIQUE (employeId, typeCongeId)
);

CREATE TABLE demande_conge (
    id                      BIGSERIAL PRIMARY KEY,
    employeId               BIGINT      NOT NULL REFERENCES employes(id),
    typeCongeId             BIGINT      NOT NULL REFERENCES type_conge(id),
    dateDebut               DATE        NOT NULL,
    dateFin                 DATE        NOT NULL,
    statut                  VARCHAR(25) NOT NULL DEFAULT 'BROUILLON'
        CHECK (statut IN (
            'BROUILLON','SOUMISE','VALIDEE_MANAGER','VALIDEE_RH','REFUSEE','ANNULEE')),
    commentaire             TEXT,
    validateurManagerId     BIGINT REFERENCES employes(id),
    dateValidationManager   TIMESTAMP,
    validateurRhId          BIGINT REFERENCES employes(id),
    dateValidationRh        TIMESTAMP,
    createdAt               TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_demande_conge_dates CHECK (dateFin >= dateDebut)
);

CREATE TABLE absence (
    id               BIGSERIAL PRIMARY KEY,
    employeId        BIGINT      NOT NULL REFERENCES employes(id),
    typeAbsence      VARCHAR(20) NOT NULL
        CHECK (typeAbsence IN ('CONGE_ANNUEL','RTT','MALADIE','SANS_SOLDE','AUTRE')),
    dateDebut        DATE        NOT NULL,
    dateFin          DATE        NOT NULL,
    demandeCongeId   BIGINT REFERENCES demande_conge(id),
    justificatifUrl  VARCHAR(255),
    justifiee        BOOLEAN     NOT NULL DEFAULT FALSE,
    createdAt        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_absence_dates CHECK (dateFin >= dateDebut)
);

CREATE INDEX idx_demande_conge_employe ON demande_conge(employeId);
CREATE INDEX idx_demande_conge_statut  ON demande_conge(statut);
CREATE INDEX idx_absence_employe       ON absence(employeId);
CREATE INDEX idx_absence_dates         ON absence(dateDebut, dateFin);

COMMIT;


-- ====================================================================
-- BLOC 8: PAIE (Moteur de calcul, Bulletin, Heures Supp, Tranches IRSA)
-- BLOC 9: EVALUATION, COMPETENCES ET FORMATION
-- BLOC 10: CARRIERE ET MOBILITE
-- BLOC 11: DISCIPLINE
-- BLOC 12: RUPTURE ET OFFBOARDING
-- ====================================================================

-- 1. Extension de la table employes pour les besoins de la paie malgache
ALTER TABLE employes ADD COLUMN IF NOT EXISTS numero_cnaps VARCHAR(50);
ALTER TABLE employes ADD COLUMN IF NOT EXISTS classification VARCHAR(20) DEFAULT 'HC';
ALTER TABLE employes ADD COLUMN IF NOT EXISTS indice NUMERIC(10,2) DEFAULT 1733;

-- BLOC 8: PAIE
CREATE TABLE IF NOT EXISTS periode_paie (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL, -- ex: '2026-11'
    mois INT NOT NULL,
    annee INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'OUVERTE', -- OUVERTE, CLOTUREE
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bulletin_paie (
    id SERIAL PRIMARY KEY,
    periode_paie_id INT NOT NULL REFERENCES periode_paie(id),
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    salaire_base NUMERIC(15,2) NOT NULL,
    taux_journalier NUMERIC(15,2) NOT NULL,
    taux_horaire NUMERIC(15,2) NOT NULL,
    classification VARCHAR(20) DEFAULT 'HC',
    indice NUMERIC(10,2) DEFAULT 1733,
    heures_normales NUMERIC(6,2) DEFAULT 173.33,
    total_hs NUMERIC(15,2) DEFAULT 0,
    total_absence_deductible NUMERIC(15,2) DEFAULT 0,
    total_primes NUMERIC(15,2) DEFAULT 0,
    salaire_brut NUMERIC(15,2) NOT NULL,
    retenue_cnaps NUMERIC(15,2) NOT NULL,
    retenue_sanitaire NUMERIC(15,2) NOT NULL,
    total_irsa NUMERIC(15,2) NOT NULL,
    total_retenues NUMERIC(15,2) NOT NULL,
    autres_indemnites NUMERIC(15,2) DEFAULT 0,
    net_a_payer NUMERIC(15,2) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'VALIDE', -- BROUILLON, VALIDE, PAYE
    date_generation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bulletin_periode_employe UNIQUE(periode_paie_id, employe_id)
);

CREATE TABLE IF NOT EXISTS calcul_hs_semaine (
    id SERIAL PRIMARY KEY,
    bulletin_paie_id INT NOT NULL REFERENCES bulletin_paie(id) ON DELETE CASCADE,
    numero_semaine INT NOT NULL, -- 1, 2, 3, 4
    heures_30 NUMERIC(6,2) DEFAULT 0,
    heures_50 NUMERIC(6,2) DEFAULT 0,
    heures_weekend NUMERIC(6,2) DEFAULT 0,
    heures_ferie NUMERIC(6,2) DEFAULT 0,
    montant_30 NUMERIC(15,2) DEFAULT 0,
    montant_50 NUMERIC(15,2) DEFAULT 0,
    montant_weekend NUMERIC(15,2) DEFAULT 0,
    montant_ferie NUMERIC(15,2) DEFAULT 0,
    total_heures NUMERIC(6,2) DEFAULT 0,
    montant_semaine NUMERIC(15,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ligne_bulletin (
    id SERIAL PRIMARY KEY,
    bulletin_paie_id INT NOT NULL REFERENCES bulletin_paie(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    designation VARCHAR(255) NOT NULL,
    nombre NUMERIC(10,2),
    taux NUMERIC(15,2),
    montant NUMERIC(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- GAIN, RETENUE, COTISATION, IRSA, INFO
    ordre INT NOT NULL
);

CREATE TABLE IF NOT EXISTS variable_paie (
    id SERIAL PRIMARY KEY,
    periode_paie_id INT NOT NULL REFERENCES periode_paie(id),
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    type VARCHAR(50) NOT NULL, -- PRIME_RENDEMENT, PRIME_ANCIENNETE, HS, ABSENCE, RAPPEL, AUTRES_INDEMNITES
    libelle VARCHAR(255) NOT NULL,
    nombre NUMERIC(10,2) DEFAULT 0,
    taux NUMERIC(15,2) DEFAULT 0,
    montant NUMERIC(15,2) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- BLOC 9: EVALUATION, COMPETENCES ET FORMATION
CREATE TABLE IF NOT EXISTS competence (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS employe_competence (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    competence_id INT NOT NULL REFERENCES competence(id),
    niveau_acquis INT NOT NULL DEFAULT 1, -- 1 à 5
    niveau_requis INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS objectif_evaluation (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    cible VARCHAR(100),
    statut VARCHAR(30) DEFAULT 'EN_COURS' -- ATTEINT, NON_ATTEINT, EN_COURS
);

CREATE TABLE IF NOT EXISTS evaluation_rh (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    evaluateur_id BIGINT NOT NULL REFERENCES employes(id),
    date_evaluation DATE NOT NULL,
    note_globale NUMERIC(4,2),
    commentaires TEXT,
    besoins_formation TEXT,
    statut VARCHAR(30) DEFAULT 'VALIDEE'
);

CREATE TABLE IF NOT EXISTS formation (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    duree_heures INT NOT NULL,
    cout NUMERIC(15,2) DEFAULT 0,
    employe_id BIGINT REFERENCES employes(id),
    date_debut DATE,
    date_fin DATE,
    statut VARCHAR(30) DEFAULT 'PLANIFIEE', -- PLANIFIEE, EN_COURS, TERMINEE
    evaluation_post_formation TEXT
);

-- BLOC 10: CARRIERE ET MOBILITE
CREATE TABLE IF NOT EXISTS historique_affectation_carriere (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    poste_id INT NOT NULL REFERENCES poste(id),
    departement_id INT REFERENCES departement(id),
    date_debut DATE NOT NULL,
    date_fin DATE,
    motif VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS promotion (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    ancien_poste_id INT NOT NULL REFERENCES poste(id),
    nouveau_poste_id INT NOT NULL REFERENCES poste(id),
    date_effet DATE NOT NULL,
    augmentation_salaire NUMERIC(15,2) DEFAULT 0,
    motif TEXT,
    statut VARCHAR(30) DEFAULT 'VALIDEE'
);

CREATE TABLE IF NOT EXISTS mutation (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    ancien_departement_id INT REFERENCES departement(id),
    nouveau_departement_id INT REFERENCES departement(id),
    date_effet DATE NOT NULL,
    motif TEXT
);

CREATE TABLE IF NOT EXISTS historique_salaire (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    ancien_salaire NUMERIC(15,2) NOT NULL,
    nouveau_salaire NUMERIC(15,2) NOT NULL,
    date_changement DATE NOT NULL,
    motif VARCHAR(255)
);

-- BLOC 11: DISCIPLINE
CREATE TABLE IF NOT EXISTS incident_disciplinaire (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    date_incident DATE NOT NULL,
    motif VARCHAR(255) NOT NULL,
    description TEXT,
    gravite VARCHAR(30) DEFAULT 'MOYENNE', -- FAIBLE, MOYENNE, GRAVE, TRES_GRAVE
    statut VARCHAR(30) DEFAULT 'EN_COURS' -- DECLARE, EN_COURS, CLOTURE
);

CREATE TABLE IF NOT EXISTS procedure_disciplinaire (
    id SERIAL PRIMARY KEY,
    incident_id INT NOT NULL REFERENCES incident_disciplinaire(id) ON DELETE CASCADE,
    date_demande_explication DATE NOT NULL,
    demande_explication TEXT NOT NULL,
    date_reponse DATE,
    reponse_salarie TEXT,
    date_decision DATE,
    decision TEXT,
    statut VARCHAR(30) DEFAULT 'DEMANDE_ENVOYEE' -- DEMANDE_ENVOYEE, REPONSE_RECUE, DECIDEE, CLOTUREE
);

CREATE TABLE IF NOT EXISTS sanction_disciplinaire (
    id SERIAL PRIMARY KEY,
    procedure_id INT NOT NULL REFERENCES procedure_disciplinaire(id) ON DELETE CASCADE,
    type_sanction VARCHAR(50) NOT NULL, -- AVERTISSEMENT_VERBAL, AVERTISSEMENT_ECRIT, MISE_A_PIED, RETROGRADATION, LICENCIEMENT_DISCIPLINAIRE
    date_notification DATE NOT NULL,
    description TEXT,
    justificatif_url VARCHAR(255)
);

-- BLOC 12: RUPTURE ET OFFBOARDING
CREATE TABLE IF NOT EXISTS rupture_contrat (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    contrat_id INT NOT NULL REFERENCES contrat(id),
    motif VARCHAR(50) NOT NULL, -- DEMISSION, LICENCIEMENT, FIN_CDD, RETRAITE, RUPTURE_CONVENTIONNELLE
    date_notification DATE NOT NULL,
    date_sortie_prevue DATE NOT NULL,
    date_sortie_effective DATE,
    preavis_effectue BOOLEAN DEFAULT true,
    jours_preavis INT DEFAULT 30,
    jours_conges_restants NUMERIC(5,2) DEFAULT 0,
    statut VARCHAR(30) DEFAULT 'EN_COURS' -- INITIÉE, PREAVIS_EN_COURS, TERMINEE
);

CREATE TABLE IF NOT EXISTS solde_tout_compte (
    id SERIAL PRIMARY KEY,
    rupture_id INT NOT NULL REFERENCES rupture_contrat(id) ON DELETE CASCADE,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    date_calcul DATE NOT NULL,
    salaire_dernier_mois NUMERIC(15,2) NOT NULL,
    indemnite_conges_payes NUMERIC(15,2) DEFAULT 0,
    indemnite_preavis NUMERIC(15,2) DEFAULT 0,
    indemnite_licenciement NUMERIC(15,2) DEFAULT 0,
    total_brut NUMERIC(15,2) NOT NULL,
    total_retenues NUMERIC(15,2) NOT NULL,
    net_solde NUMERIC(15,2) NOT NULL,
    recu_signe BOOLEAN DEFAULT false
);
