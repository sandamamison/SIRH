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
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employes
(
    id           BIGSERIAL PRIMARY KEY,
    matricule    VARCHAR(30) NOT NULL UNIQUE,
    personneid   BIGINT      NOT NULL UNIQUE REFERENCES personnes (id),
    dateembauche DATE        NOT NULL,
    statut       VARCHAR(50) NOT NULL DEFAULT 'ACTIF',
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
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

-- -- Mise à jour automatique des colonnes techniques.
-- CREATE OR REPLACE FUNCTION mettre_a_jour_updated_at()
-- RETURNS TRIGGER
-- LANGUAGE plpgsql
-- AS $$
-- BEGIN
--     NEW.updated_at = CURRENT_TIMESTAMP;
--     RETURN NEW;
-- END;
-- $$;
--
-- CREATE TRIGGER trg_personnes_updated_at
--     BEFORE UPDATE ON personnes
--     FOR EACH ROW EXECUTE FUNCTION mettre_a_jour_updated_at();
--
-- CREATE TRIGGER trg_employes_updated_at
--     BEFORE UPDATE ON employes
--     FOR EACH ROW EXECUTE FUNCTION mettre_a_jour_updated_at();
--
-- CREATE TRIGGER trg_propositions_embauche_updated_at
--     BEFORE UPDATE ON propositions_embauche
--     FOR EACH ROW EXECUTE FUNCTION mettre_a_jour_updated_at();

COMMIT;


