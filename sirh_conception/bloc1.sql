-- ============================================================================
-- SIRH - BLOC 1 : Besoin en personnel
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | identifiants JAMAIS entre guillemets
-- Enums   : VARCHAR + CHECK (enum Java = source de verite)
-- FK employe : sans contrainte pour l'instant ; le Bloc 3 ajoute les ALTER TABLE
-- ============================================================================

CREATE TABLE departement (
    id                    SERIAL PRIMARY KEY,
    codeDepartement       VARCHAR(20)  NOT NULL UNIQUE,
    nomDepartement        VARCHAR(100) NOT NULL,
    description           TEXT,
    employeResponsableId  INTEGER,                    -- FK vers employe ajoutee par le Bloc 3
    departementParentId   INTEGER REFERENCES departement(id),
    CONSTRAINT chk_departement_parent_diff
        CHECK (departementParentId IS NULL OR departementParentId <> id)
);

CREATE TABLE poste (
    id                 SERIAL PRIMARY KEY,
    codePoste          VARCHAR(30)  NOT NULL UNIQUE,
    titre              VARCHAR(100) NOT NULL,
    descriptionMission TEXT,
    salaireMin         INTEGER CHECK (salaireMin IS NULL OR salaireMin >= 0),
    salaireMax         INTEGER CHECK (salaireMax IS NULL OR salaireMax >= 0),
    departementId      INTEGER NOT NULL REFERENCES departement(id),
    CONSTRAINT chk_poste_salaires
        CHECK (salaireMin IS NULL OR salaireMax IS NULL OR salaireMax >= salaireMin)
);

CREATE TABLE demandeRecrutement (
    id                  SERIAL PRIMARY KEY,
    posteId             INTEGER NOT NULL REFERENCES poste(id),
    -- 3NF : departementId est deliberement absent (on le deduit de poste.departementId)
    motif               VARCHAR(20) NOT NULL
        CHECK (motif IN ('CREATION', 'REMPLACEMENT')),                          -- RG1-02
    justification       TEXT NOT NULL,
    employeRemplaceId   INTEGER,                                                -- RG1-03 (FK ajoutee par le Bloc 3)
    effectifDemande     INTEGER NOT NULL DEFAULT 1 CHECK (effectifDemande > 0),
    budgetPrevu         BIGINT  NOT NULL CHECK (budgetPrevu > 0),               -- RG1-04 ; BIGINT pour grands budgets Ariary
    dateSouhaitee       DATE    NOT NULL,                                       -- RG1-04
    statut              VARCHAR(30) NOT NULL DEFAULT 'BROUILLON'
        CHECK (statut IN ('BROUILLON', 'SOUMISE', 'VALIDEE_RH', 'VALIDEE_DIRECTION',
                          'RECRUTEMENT_OUVERT', 'CLOTUREE', 'REJETEE')),
    employeDemandeurId  INTEGER NOT NULL,                                       -- RG1-01 (FK ajoutee par le Bloc 3)
    dateCreation        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- RG1-03 : remplacement <==> employe remplace present (double implication)
    CONSTRAINT chk_demande_remplacement
        CHECK ((motif = 'REMPLACEMENT') = (employeRemplaceId IS NOT NULL))
);

CREATE TABLE validationDemande (
    id                    SERIAL PRIMARY KEY,
    demandeRecrutementId  INTEGER NOT NULL REFERENCES demandeRecrutement(id),
    employeValideurId     INTEGER NOT NULL,                                     -- FK ajoutee par le Bloc 3
    -- RG1-05 : seulement RH et DIRECTION (la soumission du manager est dans l'historique)
    niveau                VARCHAR(20) NOT NULL CHECK (niveau IN ('RH', 'DIRECTION')),
    decision              VARCHAR(20) NOT NULL CHECK (decision IN ('APPROUVE', 'REJETE')),
    commentaire           TEXT,
    dateDecision          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,        -- RG1-06
    -- RG1-05 : une seule decision par niveau de validation pour une demande donnee
    CONSTRAINT uq_validation_demande_niveau UNIQUE (demandeRecrutementId, niveau)
);

CREATE TABLE historiqueStatutDemande (
    id                    SERIAL PRIMARY KEY,
    demandeRecrutementId  INTEGER NOT NULL REFERENCES demandeRecrutement(id),
    ancienStatut          VARCHAR(30),                                         -- NULL a la creation initiale
    nouveauStatut         VARCHAR(30) NOT NULL,
    -- Pas de CHECK sur ancienStatut/nouveauStatut : l'historique doit survivre aux ajouts futurs d'etats
    employeActeurId       INTEGER NOT NULL,                                    -- FK ajoutee par le Bloc 3
    commentaire           TEXT,
    dateTransition        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP        -- RG1-06
);

-- Index sur les FK (PostgreSQL n'indexe pas les FK automatiquement)
CREATE INDEX idx_poste_departement      ON poste(departementId);
CREATE INDEX idx_demande_poste          ON demandeRecrutement(posteId);
CREATE INDEX idx_demande_statut         ON demandeRecrutement(statut);
CREATE INDEX idx_demande_demandeur      ON demandeRecrutement(employeDemandeurId);
CREATE INDEX idx_validation_demande     ON validationDemande(demandeRecrutementId);
CREATE INDEX idx_historique_demande     ON historiqueStatutDemande(demandeRecrutementId);
