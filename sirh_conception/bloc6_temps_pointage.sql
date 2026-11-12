-- ============================================================================
-- SIRH - BLOC 6 : Temps de travail et pointage
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- ============================================================================

CREATE TABLE IF NOT EXISTS regime_travail (
    id                 BIGSERIAL PRIMARY KEY,
    libelle            VARCHAR(80) NOT NULL UNIQUE,
    nbHeuresSemaine    INTEGER     NOT NULL CHECK (nbHeuresSemaine > 0)
);

CREATE TABLE IF NOT EXISTS horaire (
    id              BIGSERIAL PRIMARY KEY,
    libelle         VARCHAR(80) NOT NULL UNIQUE,
    heureDebut      TIME        NOT NULL,
    heureFin        TIME        NOT NULL,
    pauseMinutes    INTEGER     DEFAULT 0 CHECK (pauseMinutes >= 0),
    CONSTRAINT chk_horaire_ordre CHECK (heureFin > heureDebut)
);

CREATE TABLE IF NOT EXISTS planning (
    id                BIGSERIAL PRIMARY KEY,
    employeId         BIGINT  NOT NULL REFERENCES employes(id),
    horaireId         BIGINT  NOT NULL REFERENCES horaire(id),
    semaineDebut      DATE    NOT NULL,
    regimeTravailId   BIGINT REFERENCES regime_travail(id),
    CONSTRAINT uq_planning_employe_semaine UNIQUE (employeId, semaineDebut)
);

CREATE TABLE IF NOT EXISTS pointage (
    id          BIGSERIAL PRIMARY KEY,
    employeId   BIGINT      NOT NULL REFERENCES employes(id),
    direction   VARCHAR(10) NOT NULL CHECK (direction IN ('ENTREE', 'SORTIE')),
    horodatage  TIMESTAMP   NOT NULL,
    origine     VARCHAR(10) NOT NULL CHECK (origine IN ('BADGE', 'APPLI', 'MANUEL')),
    planningId  BIGINT REFERENCES planning(id),
    createdAt   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS anomalie_pointage (
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

CREATE INDEX IF NOT EXISTS idx_planning_employe   ON planning(employeId);
CREATE INDEX IF NOT EXISTS idx_pointage_employe   ON pointage(employeId);
CREATE INDEX IF NOT EXISTS idx_pointage_date      ON pointage(horodatage);
CREATE INDEX IF NOT EXISTS idx_anomalie_employe   ON anomalie_pointage(employeId);
