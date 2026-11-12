-- ============================================================================
-- SIRH - BLOC 5 : Journal d'audit et dossier administratif
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- ============================================================================

CREATE TABLE IF NOT EXISTS journal_audit (
    id                BIGSERIAL PRIMARY KEY,
    acteurId          BIGINT      NOT NULL REFERENCES employes(id),
    typeAction        VARCHAR(20) NOT NULL
        CHECK (typeAction IN ('CONSULTATION','MODIFICATION','CREATION','SUPPRESSION','CONNEXION')),
    tableCible        VARCHAR(100) NOT NULL,
    enregistrementId  BIGINT,
    detail            TEXT,
    dateAction        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_acteur  ON journal_audit(acteurId);
CREATE INDEX IF NOT EXISTS idx_audit_table   ON journal_audit(tableCible);
CREATE INDEX IF NOT EXISTS idx_audit_date    ON journal_audit(dateAction);
