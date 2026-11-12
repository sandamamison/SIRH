-- ============================================================================
-- SIRH - BLOC 4 : Onboarding et Documents
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- ============================================================================

CREATE TABLE IF NOT EXISTS onboarding (
    id          BIGSERIAL PRIMARY KEY,
    employe_id  BIGINT      NOT NULL REFERENCES employes(id),
    date_debut  DATE        NOT NULL,
    statut      VARCHAR(20) NOT NULL DEFAULT 'EN_COURS'
        CHECK (statut IN ('EN_COURS', 'TERMINE', 'ANNULE')),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS onboarding_tache (
    id              BIGSERIAL PRIMARY KEY,
    onboarding_id   BIGINT      NOT NULL REFERENCES onboarding(id) ON DELETE CASCADE,
    libelle         VARCHAR(150) NOT NULL,
    responsable_id  BIGINT REFERENCES employes(id),
    echeance        DATE,
    statut          VARCHAR(20) NOT NULL DEFAULT 'A_FAIRE'
        CHECK (statut IN ('A_FAIRE', 'EN_COURS', 'FAIT')),
    obligatoire     BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS document (
    id              BIGSERIAL PRIMARY KEY,
    type_document   VARCHAR(30) NOT NULL
        CHECK (type_document IN ('CONTRAT_SIGNE','PIECE_IDENTITE','DIPLOME','JUSTIFICATIF','AUTRE')),
    fichier_url     VARCHAR(255) NOT NULL,
    employe_id      BIGINT      NOT NULL REFERENCES employes(id),
    contrat_id      INTEGER REFERENCES contrat(id),
    date_depot      DATE        NOT NULL DEFAULT CURRENT_DATE,
    date_validite   DATE,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_onboarding_employe    ON onboarding(employe_id);
CREATE INDEX IF NOT EXISTS idx_onboarding_tache_ob   ON onboarding_tache(onboarding_id);
CREATE INDEX IF NOT EXISTS idx_document_employe      ON document(employe_id);
CREATE INDEX IF NOT EXISTS idx_document_contrat      ON document(contrat_id);
