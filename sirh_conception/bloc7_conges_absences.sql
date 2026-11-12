-- ============================================================================
-- SIRH - BLOC 7 : Congés et absences
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- ============================================================================

CREATE TABLE IF NOT EXISTS type_conge (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(20)  NOT NULL UNIQUE,
    libelle         VARCHAR(100) NOT NULL,
    plafonne        BOOLEAN      NOT NULL DEFAULT FALSE,
    duree_max_jours INTEGER CHECK (duree_max_jours IS NULL OR duree_max_jours > 0)
);

CREATE TABLE IF NOT EXISTS droit_conge (
    id              BIGSERIAL PRIMARY KEY,
    employe_id      BIGINT  NOT NULL REFERENCES employes(id),
    type_conge_id   BIGINT  NOT NULL REFERENCES type_conge(id),
    annee           INTEGER NOT NULL CHECK (annee >= 2000),
    jours_acquis    NUMERIC(5,1) NOT NULL CHECK (jours_acquis >= 0),
    CONSTRAINT uq_droit_conge UNIQUE (employe_id, type_conge_id, annee)
);

CREATE TABLE IF NOT EXISTS solde_conge (
    id              BIGSERIAL PRIMARY KEY,
    employe_id      BIGINT         NOT NULL REFERENCES employes(id),
    type_conge_id   BIGINT         NOT NULL REFERENCES type_conge(id),
    solde_courant   NUMERIC(5,1)   NOT NULL DEFAULT 0 CHECK (solde_courant >= 0),
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_solde_conge UNIQUE (employe_id, type_conge_id)
);

CREATE TABLE IF NOT EXISTS demande_conge (
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

CREATE TABLE IF NOT EXISTS absence (
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

CREATE INDEX IF NOT EXISTS idx_demande_conge_employe ON demande_conge(employe_id);
CREATE INDEX IF NOT EXISTS idx_demande_conge_statut  ON demande_conge(statut);
CREATE INDEX IF NOT EXISTS idx_absence_employe       ON absence(employe_id);
CREATE INDEX IF NOT EXISTS idx_absence_dates         ON absence(date_debut, date_fin);
