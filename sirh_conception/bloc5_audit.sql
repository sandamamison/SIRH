-- ============================================================================
-- SIRH - BLOC 5 : Journal d'audit et dossier administratif
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- ============================================================================

CREATE TABLE IF NOT EXISTS journal_audit (
    id                BIGSERIAL PRIMARY KEY,
    acteur_id         BIGINT      NOT NULL REFERENCES employes(id),
    type_action       VARCHAR(20) NOT NULL
        CHECK (type_action IN ('CONSULTATION','MODIFICATION','CREATION','SUPPRESSION','CONNEXION')),
    table_cible       VARCHAR(100) NOT NULL,
    enregistrement_id BIGINT,
    detail            TEXT,
    date_action       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_acteur  ON journal_audit(acteur_id);
CREATE INDEX IF NOT EXISTS idx_audit_table   ON journal_audit(table_cible);
CREATE INDEX IF NOT EXISTS idx_audit_date    ON journal_audit(date_action);
