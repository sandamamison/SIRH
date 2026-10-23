-- ============================================================================
-- SIRH - BLOC 4 : Contrats et Affectations
-- Base    : sirh
-- Norme   : PK = id | FK = (table)Id | sans guillemets
-- Enums   : VARCHAR + CHECK (enum Java = source de verite)
-- FK employe : sans contrainte pour l'instant ; merge Bloc 3 ajoute les ALTER TABLE
-- ============================================================================

-- Extension requise pour les contraintes d'exclusion temporelles (non-chevauchement)
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE contrat (
    id                    SERIAL PRIMARY KEY,
    numeroContrat         VARCHAR(50) NOT NULL UNIQUE,
    employeId             INTEGER NOT NULL,                        -- FK ajoutee par le Bloc 3
    typeContrat           VARCHAR(10) NOT NULL DEFAULT 'CE'
        CHECK (typeContrat IN ('CE', 'CDD', 'CDI')),
    dateDebut             DATE NOT NULL,
    dateFin               DATE,
    salaireBrut           INTEGER NOT NULL CHECK (salaireBrut > 0),
    statut                VARCHAR(20) NOT NULL DEFAULT 'CREE'
        CHECK (statut IN ('CREE', 'SIGNE', 'RENOUVELE', 'NON_RENOUVELE', 'TERMINE', 'ROMPU')),
    contratPrecedentId    INTEGER,                                 -- RG4-03 : renouvellement
    employeCreateurId     INTEGER NOT NULL,                        -- FK ajoutee par le Bloc 3
    employeSignataireId   INTEGER,                                 -- FK ajoutee par le Bloc 3
    dateSignature         DATE,
    dateCreation          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- RG4-01 : CDI <=> dateFin IS NULL (et CE/CDD ont obligatoirement une dateFin)
    CONSTRAINT chk_contrat_fin_selon_type
        CHECK ((typeContrat = 'CDI') = (dateFin IS NULL)),
    CONSTRAINT chk_dates_contrat
        CHECK (dateFin IS NULL OR dateFin >= dateDebut),
    -- Signature obligatoire des lors que le contrat est actif / termine
    -- APRÈS (corrigé)
    CONSTRAINT chk_contrat_signature
        CHECK ((statut IN ('SIGNE', 'RENOUVELE', 'NON_RENOUVELE', 'TERMINE', 'ROMPU'))
           = (dateSignature IS NOT NULL AND employeSignataireId IS NOT NULL)),
    CONSTRAINT chk_contrat_precedent_diff
        CHECK (contratPrecedentId IS NULL OR contratPrecedentId <> id),

    -- RG4-03 : garantit que le contrat precedent appartient au MEME employe
    -- et qu'un contrat ne peut etre renouvele qu'une seule fois
    CONSTRAINT uq_contrat_id_employe UNIQUE (id, employeId),
    CONSTRAINT uq_contrat_successeur UNIQUE (contratPrecedentId),
    CONSTRAINT fk_contrat_precedent_meme_employe
        FOREIGN KEY (contratPrecedentId, employeId) REFERENCES contrat(id, employeId),

    -- RG4-02 : non-chevauchement des periodes contractuelles d'un employe
    CONSTRAINT ex_contrat_sans_chevauchement
        EXCLUDE USING gist (employeId WITH =, daterange(dateDebut, dateFin, '[]') WITH &&)
);

CREATE TABLE affectation (
    id              SERIAL PRIMARY KEY,
    employeId       INTEGER NOT NULL,                              -- FK ajoutee par le Bloc 3
    posteId         INTEGER NOT NULL REFERENCES poste(id),
    dateDebut       DATE NOT NULL,
    dateFin         DATE,

    CONSTRAINT chk_dates_affectation
        CHECK (dateFin IS NULL OR dateFin >= dateDebut),
    -- RG4-04 : un employe ne peut etre affecte a deux postes en meme temps
    CONSTRAINT ex_affectation_sans_chevauchement
        EXCLUDE USING gist (employeId WITH =, daterange(dateDebut, dateFin, '[]') WITH &&)
);

-- Index sur les FK (PostgreSQL n'indexe pas les FK automatiquement)
CREATE INDEX idx_contrat_employe       ON contrat(employeId);
CREATE INDEX idx_contrat_precedent     ON contrat(contratPrecedentId);
CREATE INDEX idx_affectation_employe   ON affectation(employeId);
CREATE INDEX idx_affectation_poste     ON affectation(posteId);

-- ============================================================================
-- ALTER TABLE a executer APRES le merge du Bloc 3 (FK vers employe)
-- ============================================================================
-- ALTER TABLE contrat
--     ADD CONSTRAINT fk_contrat_employe
--         FOREIGN KEY (employeId) REFERENCES employe(id),
--     ADD CONSTRAINT fk_contrat_createur
--         FOREIGN KEY (employeCreateurId) REFERENCES employe(id),
--     ADD CONSTRAINT fk_contrat_signataire
--         FOREIGN KEY (employeSignataireId) REFERENCES employe(id);
--
-- ALTER TABLE affectation
--     ADD CONSTRAINT fk_affectation_employe
--         FOREIGN KEY (employeId) REFERENCES employe(id);