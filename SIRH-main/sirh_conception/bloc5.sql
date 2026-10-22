-- ============================================================================
-- SIRH - BLOC 5 : Vie du salarie - Gestion du personnel selon la structure
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | identifiants JAMAIS entre guillemets
-- Pre-requis : bloc1.sql (departement)
-- ============================================================================
-- Structure : departement > service > section
-- Personnel : employe rattache a EXACTEMENT une unite (RG5-04)
-- NB : la table employe est volontairement minimale (matricule + identite +
--      rattachement). Le Bloc 3 l'enrichira (personneId, propositionId...).
-- ============================================================================

CREATE TABLE service (
    id                    SERIAL PRIMARY KEY,
    codeService           VARCHAR(20)  NOT NULL UNIQUE,                        -- RG5-01
    nomService            VARCHAR(100) NOT NULL,
    description           TEXT,
    departementId         INTEGER NOT NULL REFERENCES departement(id),         -- RG5-02
    employeResponsableId  INTEGER                    -- FK vers employe ajoutee ci-dessous
);

CREATE TABLE section (
    id                    SERIAL PRIMARY KEY,
    codeSection           VARCHAR(20)  NOT NULL UNIQUE,                        -- RG5-01
    nomSection            VARCHAR(100) NOT NULL,
    description           TEXT,
    serviceId             INTEGER NOT NULL REFERENCES service(id),             -- RG5-02
    employeResponsableId  INTEGER                    -- FK vers employe ajoutee ci-dessous
);

CREATE TABLE employe (
    id                  SERIAL PRIMARY KEY,
    matricule           VARCHAR(30)  NOT NULL UNIQUE,                          -- RG5-05
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    email               VARCHAR(150) UNIQUE,                                   -- RG5-05 (nullable : compte cree a l'onboarding)
    telephone           VARCHAR(30),
    dateEmbauche        DATE NOT NULL,
    actif               BOOLEAN NOT NULL DEFAULT TRUE,
    employeManagerId    INTEGER REFERENCES employe(id),                        -- hierarchie (auto-reference)
    departementId       INTEGER REFERENCES departement(id),
    serviceId           INTEGER REFERENCES service(id),
    sectionId           INTEGER REFERENCES section(id),
    dateCreation        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,          -- tracabilite
    CONSTRAINT chk_employe_manager_diff
        CHECK (employeManagerId IS NULL OR employeManagerId <> id),            -- RG5-06
    -- RG5-04 : rattachement a exactement une unite organisationnelle
    CONSTRAINT chk_employe_une_unite
        CHECK ((departementId IS NOT NULL)::int
             + (serviceId    IS NOT NULL)::int
             + (sectionId    IS NOT NULL)::int = 1)
);

-- ----------------------------------------------------------------------------
-- FK responsables : ajoutees apres la creation de employe (references croisees)
-- ----------------------------------------------------------------------------
ALTER TABLE service
    ADD CONSTRAINT fk_service_responsable
    FOREIGN KEY (employeResponsableId) REFERENCES employe(id);                 -- RG5-03

ALTER TABLE section
    ADD CONSTRAINT fk_section_responsable
    FOREIGN KEY (employeResponsableId) REFERENCES employe(id);                 -- RG5-03

ALTER TABLE departement
    ADD CONSTRAINT fk_departement_responsable
    FOREIGN KEY (employeResponsableId) REFERENCES employe(id);                 -- RG5-03

-- ----------------------------------------------------------------------------
-- FK du Bloc 1 vers employe (annoncees "ajoutee par le Bloc 3" dans bloc1.sql)
-- ----------------------------------------------------------------------------
ALTER TABLE demandeRecrutement
    ADD CONSTRAINT fk_demande_demandeur
    FOREIGN KEY (employeDemandeurId) REFERENCES employe(id),
    ADD CONSTRAINT fk_demande_remplace
    FOREIGN KEY (employeRemplaceId) REFERENCES employe(id);

ALTER TABLE validationDemande
    ADD CONSTRAINT fk_validation_valideur
    FOREIGN KEY (employeValideurId) REFERENCES employe(id);

ALTER TABLE historiqueStatutDemande
    ADD CONSTRAINT fk_histo_demande_acteur
    FOREIGN KEY (employeActeurId) REFERENCES employe(id);

-- Index sur les FK (PostgreSQL n'indexe pas les FK automatiquement)
CREATE INDEX idx_service_departement ON service(departementId);
CREATE INDEX idx_section_service     ON section(serviceId);
CREATE INDEX idx_employe_departement ON employe(departementId);
CREATE INDEX idx_employe_service     ON employe(serviceId);
CREATE INDEX idx_employe_section     ON employe(sectionId);
CREATE INDEX idx_employe_manager     ON employe(employeManagerId);
