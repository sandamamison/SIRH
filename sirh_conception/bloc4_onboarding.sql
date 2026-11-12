-- ============================================================================
-- SIRH - BLOC 4 : Onboarding et Documents
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- ============================================================================

CREATE TABLE IF NOT EXISTS onboarding (
    id          BIGSERIAL PRIMARY KEY,
    employeId   BIGINT      NOT NULL REFERENCES employes(id),
    dateDebut   DATE        NOT NULL,
    statut      VARCHAR(20) NOT NULL DEFAULT 'EN_COURS'
        CHECK (statut IN ('EN_COURS', 'TERMINE', 'ANNULE')),
    createdAt   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS onboarding_tache (
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

CREATE TABLE IF NOT EXISTS document (
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

CREATE INDEX IF NOT EXISTS idx_onboarding_employe    ON onboarding(employeId);
CREATE INDEX IF NOT EXISTS idx_onboarding_tache_ob   ON onboarding_tache(onboardingId);
CREATE INDEX IF NOT EXISTS idx_document_employe      ON document(employeId);
CREATE INDEX IF NOT EXISTS idx_document_contrat      ON document(contratId);
