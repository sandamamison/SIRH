-- ============================================================================
-- SIRH - BLOC 6 : Temps de travail et pointage
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- ============================================================================

CREATE TABLE IF NOT EXISTS regime_travail (
    id                 BIGSERIAL PRIMARY KEY,
    libelle            VARCHAR(80) NOT NULL UNIQUE,
    nb_heures_semaine  INTEGER     NOT NULL CHECK (nb_heures_semaine > 0)
);

CREATE TABLE IF NOT EXISTS horaire (
    id              BIGSERIAL PRIMARY KEY,
    libelle         VARCHAR(80) NOT NULL UNIQUE,
    heure_debut     TIME        NOT NULL,
    heure_fin       TIME        NOT NULL,
    pause_minutes   INTEGER     DEFAULT 0 CHECK (pause_minutes >= 0),
    CONSTRAINT chk_horaire_ordre CHECK (heure_fin > heure_debut)
);

CREATE TABLE IF NOT EXISTS planning (
    id                BIGSERIAL PRIMARY KEY,
    employe_id        BIGINT  NOT NULL REFERENCES employes(id),
    horaire_id        BIGINT  NOT NULL REFERENCES horaire(id),
    semaine_debut     DATE    NOT NULL,
    regime_travail_id BIGINT REFERENCES regime_travail(id),
    CONSTRAINT uq_planning_employe_semaine UNIQUE (employe_id, semaine_debut)
);

CREATE TABLE IF NOT EXISTS pointage (
    id          BIGSERIAL PRIMARY KEY,
    employe_id  BIGINT      NOT NULL REFERENCES employes(id),
    direction   VARCHAR(10) NOT NULL CHECK (direction IN ('ENTREE', 'SORTIE')),
    horodatage  TIMESTAMP   NOT NULL,
    origine     VARCHAR(10) NOT NULL CHECK (origine IN ('BADGE', 'APPLI', 'MANUEL')),
    planning_id BIGINT REFERENCES planning(id),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS anomalie_pointage (
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

CREATE INDEX IF NOT EXISTS idx_planning_employe   ON planning(employe_id);
CREATE INDEX IF NOT EXISTS idx_pointage_employe   ON pointage(employe_id);
CREATE INDEX IF NOT EXISTS idx_pointage_date      ON pointage(horodatage);
CREATE INDEX IF NOT EXISTS idx_anomalie_employe   ON anomalie_pointage(employe_id);
