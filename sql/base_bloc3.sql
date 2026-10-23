-- Schéma PostgreSQL minimal pour le bloc 3 du SIRH.
-- Le script est compatible avec les entités Java actuelles.

CREATE TABLE IF NOT EXISTS personnes
(
    id         BIGSERIAL PRIMARY KEY,
    nom        VARCHAR(100) NOT NULL,
    prenom     VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    telephone  VARCHAR(30),
    adresse    VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cette table est créée ici uniquement pour permettre l'import du bloc 3 seul.
-- Si elle existe déjà, sa définition existante est conservée.
CREATE TABLE IF NOT EXISTS candidatures
(
    id          BIGSERIAL PRIMARY KEY,
    personne_id BIGINT      NOT NULL REFERENCES personnes (id),
    statut      VARCHAR(20) NOT NULL DEFAULT 'RECUE',
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS propositions_embauche
(
    id              BIGSERIAL PRIMARY KEY,
    candidature_id  BIGINT      NOT NULL UNIQUE REFERENCES candidatures (id),
    statut          VARCHAR(20) NOT NULL DEFAULT 'ENVOYEE',
    date_envoi      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_reponse    TIMESTAMP,
    date_expiration DATE,
    commentaire     TEXT,
    created_by      VARCHAR(100),
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_proposition_statut
        CHECK (statut IN ('ENVOYEE', 'ACCEPTEE', 'REFUSEE', 'EXPIREE')),
    CONSTRAINT chk_proposition_reponse
        CHECK (statut NOT IN ('ACCEPTEE', 'REFUSEE') OR date_reponse IS NOT NULL),
    CONSTRAINT chk_proposition_dates
        CHECK (date_reponse IS NULL OR date_reponse >= date_envoi)
);

CREATE TABLE IF NOT EXISTS historique_propositions
(
    id               BIGSERIAL PRIMARY KEY,
    proposition_id   BIGINT      NOT NULL REFERENCES propositions_embauche (id),
    ancien_statut    VARCHAR(20),
    nouveau_statut   VARCHAR(20) NOT NULL,
    date_transition TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    auteur           VARCHAR(100),
    commentaire      TEXT,
    CONSTRAINT chk_historique_ancien_statut
        CHECK (ancien_statut IS NULL OR ancien_statut IN ('ENVOYEE', 'ACCEPTEE', 'REFUSEE', 'EXPIREE')),
    CONSTRAINT chk_historique_nouveau_statut
        CHECK (nouveau_statut IN ('ENVOYEE', 'ACCEPTEE', 'REFUSEE', 'EXPIREE'))
);

CREATE TABLE IF NOT EXISTS employes
(
    id            BIGSERIAL PRIMARY KEY,
    matricule     VARCHAR(30) NOT NULL UNIQUE,
    personne_id   BIGINT      NOT NULL UNIQUE REFERENCES personnes (id),
    date_embauche DATE        NOT NULL,
    statut        VARCHAR(50) NOT NULL DEFAULT 'ACTIF',
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_employe_statut
        CHECK (statut IN ('ACTIF', 'INACTIF'))
);

CREATE OR REPLACE FUNCTION mettre_a_jour_updated_at()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_personnes_updated_at ON personnes;
CREATE TRIGGER trg_personnes_updated_at
    BEFORE UPDATE
    ON personnes
    FOR EACH ROW
EXECUTE FUNCTION mettre_a_jour_updated_at();

DROP TRIGGER IF EXISTS trg_candidatures_updated_at ON candidatures;
CREATE TRIGGER trg_candidatures_updated_at
    BEFORE UPDATE
    ON candidatures
    FOR EACH ROW
EXECUTE FUNCTION mettre_a_jour_updated_at();

DROP TRIGGER IF EXISTS trg_propositions_embauche_updated_at ON propositions_embauche;
CREATE TRIGGER trg_propositions_embauche_updated_at
    BEFORE UPDATE
    ON propositions_embauche
    FOR EACH ROW
EXECUTE FUNCTION mettre_a_jour_updated_at();

DROP TRIGGER IF EXISTS trg_employes_updated_at ON employes;
CREATE TRIGGER trg_employes_updated_at
    BEFORE UPDATE
    ON employes
    FOR EACH ROW
EXECUTE FUNCTION mettre_a_jour_updated_at();