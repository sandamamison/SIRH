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
    dureeMaxJours   INTEGER CHECK (dureeMaxJours IS NULL OR dureeMaxJours > 0)
);

CREATE TABLE IF NOT EXISTS droit_conge (
    id              BIGSERIAL PRIMARY KEY,
    employeId       BIGINT  NOT NULL REFERENCES employes(id),
    typeCongeId     BIGINT  NOT NULL REFERENCES type_conge(id),
    annee           INTEGER NOT NULL CHECK (annee >= 2000),
    joursAcquis     NUMERIC(5,1) NOT NULL CHECK (joursAcquis >= 0),
    CONSTRAINT uq_droit_conge UNIQUE (employeId, typeCongeId, annee)
);

CREATE TABLE IF NOT EXISTS solde_conge (
    id              BIGSERIAL PRIMARY KEY,
    employeId       BIGINT         NOT NULL REFERENCES employes(id),
    typeCongeId     BIGINT         NOT NULL REFERENCES type_conge(id),
    soldeCourant    NUMERIC(5,1)   NOT NULL DEFAULT 0 CHECK (soldeCourant >= 0),
    updatedAt       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_solde_conge UNIQUE (employeId, typeCongeId)
);

CREATE TABLE IF NOT EXISTS demande_conge (
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

CREATE TABLE IF NOT EXISTS absence (
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

CREATE INDEX IF NOT EXISTS idx_demande_conge_employe ON demande_conge(employeId);
CREATE INDEX IF NOT EXISTS idx_demande_conge_statut  ON demande_conge(statut);
CREATE INDEX IF NOT EXISTS idx_absence_employe       ON absence(employeId);
CREATE INDEX IF NOT EXISTS idx_absence_dates         ON absence(dateDebut, dateFin);
