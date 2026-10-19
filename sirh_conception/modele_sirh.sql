-- ============================================================================
-- SCHEMA RELATIONNEL POSTGRESQL - SIRH (Gestion des Ressources Humaines)
-- Conception basee sur les specifications : SIRH.md et todo_sirh.md
-- Conforme au guide regles_conception_bdd.md (Audits 1 & 2)
-- ============================================================================

-- Extension requise pour les contraintes d'exclusion temporelles (non-chevauchement)
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- ----------------------------------------------------------------------------
-- TYPES ENUMERES POUR LES STATUTS ET TYPES METIERS
-- ----------------------------------------------------------------------------

-- Workflow : Brouillon -> Soumise -> Validee RH -> Validee Direction -> Recrutement Ouvert -> Cloturee / Rejetee
CREATE TYPE statut_demande AS ENUM (
    'BROUILLON', 
    'SOUMISE', 
    'VALIDEE_RH', 
    'VALIDEE_DIRECTION', 
    'RECRUTEMENT_OUVERT', 
    'CLOTUREE', 
    'REJETEE'
);

CREATE TYPE motif_recrutement AS ENUM (
    'CREATION', 
    'REMPLACEMENT'
);

CREATE TYPE decision_validation AS ENUM (
    'APPROUVE', 
    'REJETE'
);

CREATE TYPE statut_offre AS ENUM (
    'BROUILLON', 
    'PUBLIEE', 
    'CLOTUREE', 
    'ANNULEE'
);

CREATE TYPE statut_candidature AS ENUM (
    'RECUE', 
    'PRESELECTIONNEE', 
    'EN_ENTRETIEN', 
    'RETENUE', 
    'REJETEE'
);

CREATE TYPE type_entretien_enum AS ENUM (
    'RH', 
    'TECHNIQUE', 
    'DIRECTION', 
    'TEST'
);

CREATE TYPE statut_entretien AS ENUM (
    'PLANIFIE', 
    'EFFECTUE', 
    'ANNULE', 
    'REPORTE'
);

CREATE TYPE avis_evaluation AS ENUM (
    'FAVORABLE', 
    'RESERVE', 
    'DEFAVORABLE'
);

-- RG3-02 : ENVOYEE, ACCEPTEE, REFUSEE, EXPIREE
CREATE TYPE statut_proposition AS ENUM (
    'ENVOYEE', 
    'ACCEPTEE', 
    'REFUSEE', 
    'EXPIREE'
);

CREATE TYPE type_contrat_enum AS ENUM (
    'CE',   -- Contrat d'Essai (6 mois, 1 renouvellement max)
    'CDD',  -- Contrat a Duree Determinee (2 ans max)
    'CDI'   -- Contrat a Duree Indeterminee
);

CREATE TYPE statut_contrat AS ENUM (
    'CREE', 
    'SIGNE', 
    'RENOUVELE', 
    'NON_RENOUVELE', 
    'TERMINE', 
    'ROMPU'
);

CREATE TYPE statut_tache AS ENUM (
    'A_FAIRE', 
    'EN_COURS', 
    'FAIT', 
    'ANNULE'
);

CREATE TYPE role_utilisateur AS ENUM (
    'CANDIDAT',
    'SALARIE', 
    'MANAGER', 
    'RH', 
    'DIRECTION', 
    'ADMIN'
);

CREATE TYPE type_document_enum AS ENUM (
    'CONTRAT_SIGNE', 
    'CV', 
    'LETTRE_MOTIVATION', 
    'PIECE_IDENTITE', 
    'DIPLOME', 
    'RIB', 
    'AUTRE'
);

-- ============================================================================
-- BLOC 0 : IDENTITE COMMUNE (CANDIDAT != EMPLOYE, MAIS MEME PERSONNE PHYSIQUE)
-- ============================================================================

CREATE TABLE PERSONNE (
    id_personne SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telephone VARCHAR(30),
    adresse TEXT,
    date_naissance DATE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_date_naissance CHECK (date_naissance IS NULL OR date_naissance < CURRENT_DATE)
);

-- Unicite de l'email insensible a la casse (Regle 12)
CREATE UNIQUE INDEX uq_personne_email_lower ON PERSONNE (lower(email));

-- ============================================================================
-- BLOC 1 : STRUCTURE ORGANISATIONNELLE & BESOIN EN PERSONNEL
-- ============================================================================

CREATE TABLE DEPARTEMENT (
    id_departement SERIAL PRIMARY KEY,
    code_departement VARCHAR(20) UNIQUE NOT NULL,
    nom_departement VARCHAR(100) NOT NULL,
    description TEXT,
    id_responsable INTEGER,                  -- Salarie chef de dept (FK liee a EMPLOYE)
    id_departement_parent INTEGER REFERENCES DEPARTEMENT(id_departement), -- Hierarchie organisationnelle
    CONSTRAINT chk_dept_parent_diff CHECK (id_departement_parent IS NULL OR id_departement_parent <> id_departement)
);

CREATE TABLE POSTE (
    id_poste SERIAL PRIMARY KEY,
    code_poste VARCHAR(30) UNIQUE NOT NULL,
    titre VARCHAR(100) NOT NULL,
    description_mission TEXT,
    salaire_min INTEGER CHECK (salaire_min IS NULL OR salaire_min >= 0),
    salaire_max INTEGER CHECK (salaire_max IS NULL OR salaire_max >= 0),
    id_departement INTEGER NOT NULL REFERENCES DEPARTEMENT(id_departement),
    CONSTRAINT chk_poste_salaires CHECK (
        salaire_min IS NULL OR salaire_max IS NULL OR salaire_max >= salaire_min
    )
);

CREATE TABLE DEMANDE_RECRUTEMENT (
    id_demande SERIAL PRIMARY KEY,
    id_poste INTEGER NOT NULL REFERENCES POSTE(id_poste),
    -- Normalisation 3NF : id_departement supprime car determine par id_poste
    motif motif_recrutement NOT NULL,        -- RG1-02: 'CREATION' ou 'REMPLACEMENT'
    justification TEXT NOT NULL,             -- Explication detaillee du besoin
    id_employe_remplace INTEGER,             -- RG1-03: reference le salarie remplace (FK liee a EMPLOYE)
    effectif_demande INTEGER NOT NULL DEFAULT 1 CHECK (effectif_demande > 0),
    budget_prevu INTEGER NOT NULL CHECK (budget_prevu > 0),
    date_souhaitee DATE NOT NULL,
    statut statut_demande NOT NULL DEFAULT 'BROUILLON',
    id_demandeur INTEGER NOT NULL,           -- Manager emetteur (FK liee a EMPLOYE)
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- RG1-03 : remplacement obligatoire ssi motif = REMPLACEMENT
    CONSTRAINT chk_demande_remplacement CHECK ((motif = 'REMPLACEMENT') = (id_employe_remplace IS NOT NULL))
);

CREATE TABLE VALIDATION_DEMANDE (
    id_validation SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL REFERENCES DEMANDE_RECRUTEMENT(id_demande),
    id_valideur INTEGER NOT NULL,            -- Salarie valideur (FK liee a EMPLOYE)
    niveau_role role_utilisateur NOT NULL CHECK (niveau_role IN ('MANAGER', 'RH', 'DIRECTION')),
    decision decision_validation NOT NULL,
    commentaire TEXT,
    date_decision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- RG1-05/06: Une seule decision par niveau de role pour une demande donnee
    CONSTRAINT uq_demande_niveau UNIQUE (id_demande, niveau_role)
);

-- Historisation detaillee des etapes du workflow de demande (Defaut 4)
CREATE TABLE HISTORIQUE_STATUT_DEMANDE (
    id_historique SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL REFERENCES DEMANDE_RECRUTEMENT(id_demande),
    ancien_statut statut_demande,
    nouveau_statut statut_demande NOT NULL,
    id_acteur INTEGER NOT NULL,              -- Salarie auteur du changement (FK liee a EMPLOYE)
    commentaire TEXT,
    date_transition TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- BLOC 2 : RECRUTEMENT
-- ============================================================================

CREATE TABLE OFFRE_EMPLOI (
    id_offre SERIAL PRIMARY KEY,
    -- 1:1 : Une demande de recrutement validee engendre au maximum une offre d'emploi
    id_demande INTEGER UNIQUE NOT NULL REFERENCES DEMANDE_RECRUTEMENT(id_demande),
    -- Normalisation 3NF : id_poste supprime car determine par DEMANDE_RECRUTEMENT
    titre_offre VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    salaire_propose INTEGER CHECK (salaire_propose IS NULL OR salaire_propose > 0),
    horaires_travail VARCHAR(100),           -- ex: "Lundi-Vendredi 8h-17h"
    lieu_travail VARCHAR(150) NOT NULL,
    date_publication DATE,
    date_cloture DATE,
    statut statut_offre NOT NULL DEFAULT 'BROUILLON',
    CONSTRAINT chk_dates_offre CHECK (
        date_cloture IS NULL OR date_publication IS NULL OR date_cloture >= date_publication
    ),
    CONSTRAINT chk_offre_publiee CHECK (statut <> 'PUBLIEE' OR date_publication IS NOT NULL),
    CONSTRAINT chk_offre_cloturee CHECK (statut <> 'CLOTUREE' OR date_cloture IS NOT NULL)
);

CREATE TABLE CANDIDATURE (
    id_candidature SERIAL PRIMARY KEY,
    id_offre INTEGER NOT NULL REFERENCES OFFRE_EMPLOI(id_offre),
    id_personne INTEGER NOT NULL REFERENCES PERSONNE(id_personne),
    date_candidature TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cv_url VARCHAR(255),
    lettre_motivation TEXT,
    pretention_salariale INTEGER CHECK (pretention_salariale IS NULL OR pretention_salariale > 0),
    accord_salaire BOOLEAN,                  -- salaire propose : ok / non
    remarque_salaire TEXT,
    statut statut_candidature NOT NULL DEFAULT 'RECUE',
    -- RG2: une seule candidature par offre et par personne
    CONSTRAINT uq_candidat_offre UNIQUE (id_offre, id_personne)
);

-- Historisation detaillee des etapes de candidature et decision finale (Defaut 4 / RG2-07)
CREATE TABLE HISTORIQUE_STATUT_CANDIDATURE (
    id_historique SERIAL PRIMARY KEY,
    id_candidature INTEGER NOT NULL REFERENCES CANDIDATURE(id_candidature),
    ancien_statut statut_candidature,
    nouveau_statut statut_candidature NOT NULL,
    id_acteur INTEGER NOT NULL,              -- Salarie RH auteur de la transition (FK liee a EMPLOYE)
    commentaire TEXT,
    date_transition TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ENTRETIEN (
    id_entretien SERIAL PRIMARY KEY,
    id_candidature INTEGER NOT NULL REFERENCES CANDIDATURE(id_candidature),
    date_heure TIMESTAMP NOT NULL,
    lieu_ou_lien VARCHAR(255),
    type_entretien type_entretien_enum NOT NULL DEFAULT 'RH',
    statut statut_entretien NOT NULL DEFAULT 'PLANIFIE'
);

-- Intervenants / jury convoques pour l'entretien (Section C: Mineurs)
CREATE TABLE INTERVENANT_ENTRETIEN (
    id_entretien INTEGER NOT NULL REFERENCES ENTRETIEN(id_entretien) ON DELETE CASCADE,
    id_employe INTEGER NOT NULL,             -- Salarie jury (FK liee a EMPLOYE)
    role_intervenant VARCHAR(50) DEFAULT 'EXAMINATEUR',
    PRIMARY KEY (id_entretien, id_employe)
);

CREATE TABLE EVALUATION_CANDIDAT (
    id_evaluation SERIAL PRIMARY KEY,
    id_entretien INTEGER NOT NULL REFERENCES ENTRETIEN(id_entretien),
    id_evaluateur INTEGER NOT NULL,          -- Salarie evaluateur (FK liee a EMPLOYE)
    score_global INTEGER CHECK (score_global BETWEEN 0 AND 20),
    points_forts TEXT,
    points_faibles TEXT,
    avis avis_evaluation NOT NULL,
    date_evaluation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- RG2-06 : un evaluateur depose une evaluation unique par entretien
    CONSTRAINT uq_entretien_evaluateur UNIQUE (id_entretien, id_evaluateur)
);

-- ============================================================================
-- BLOC 3 : SELECTION & DOSSIER SALARIE
-- ============================================================================

CREATE TABLE PROPOSITION_EMBAUCHE (
    id_proposition SERIAL PRIMARY KEY,
    id_candidature INTEGER UNIQUE NOT NULL REFERENCES CANDIDATURE(id_candidature),
    salaire_mensuel INTEGER NOT NULL CHECK (salaire_mensuel > 0),
    date_debut_prevue DATE NOT NULL,
    date_emission DATE NOT NULL DEFAULT CURRENT_DATE,
    date_reponse DATE,
    statut statut_proposition NOT NULL DEFAULT 'ENVOYEE',
    CONSTRAINT chk_dates_proposition CHECK (
        (date_reponse IS NULL OR date_reponse >= date_emission) AND
        date_debut_prevue >= date_emission
    ),
    CONSTRAINT chk_proposition_reponse CHECK (
        statut NOT IN ('ACCEPTEE', 'REFUSEE') OR date_reponse IS NOT NULL
    )
);

CREATE TABLE HISTORIQUE_STATUT_PROPOSITION (
    id_historique SERIAL PRIMARY KEY,
    id_proposition INTEGER NOT NULL REFERENCES PROPOSITION_EMBAUCHE(id_proposition),
    ancien_statut statut_proposition,
    nouveau_statut statut_proposition NOT NULL,
    id_acteur INTEGER,                       -- Salarie auteur (FK liee a EMPLOYE)
    commentaire TEXT,
    date_transition TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE EMPLOYE (
    id_employe SERIAL PRIMARY KEY,
    matricule VARCHAR(30) UNIQUE NOT NULL,   -- ex: EMP-2026-0001
    id_personne INTEGER UNIQUE NOT NULL REFERENCES PERSONNE(id_personne),
    -- Tracabilite RG3-03/04 : lien vers la proposition d'embauche a l'origine de l'entree
    id_proposition INTEGER UNIQUE REFERENCES PROPOSITION_EMBAUCHE(id_proposition),
    id_manager INTEGER REFERENCES EMPLOYE(id_employe), -- Auto-reference hierarchique (Regle 10)
    date_embauche DATE NOT NULL,
    compte_bancaire VARCHAR(50),             -- RIB/IBAN (optionnel)
    numero_securite_sociale VARCHAR(50) UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_employe_manager_diff CHECK (id_manager IS NULL OR id_manager <> id_employe)
);

-- ----------------------------------------------------------------------------
-- RESOLUTION DES CLES ETRANGERES RETROACTIVES VERS EMPLOYE
-- (Preserve l'ordre logique d'exposition par bloc pedagogique du cours)
-- ----------------------------------------------------------------------------

ALTER TABLE DEPARTEMENT
    ADD CONSTRAINT fk_dept_responsable FOREIGN KEY (id_responsable) REFERENCES EMPLOYE(id_employe);

ALTER TABLE DEMANDE_RECRUTEMENT 
    ADD CONSTRAINT fk_demande_demandeur FOREIGN KEY (id_demandeur) REFERENCES EMPLOYE(id_employe),
    ADD CONSTRAINT fk_demande_remplace FOREIGN KEY (id_employe_remplace) REFERENCES EMPLOYE(id_employe);

ALTER TABLE VALIDATION_DEMANDE 
    ADD CONSTRAINT fk_validation_valideur FOREIGN KEY (id_valideur) REFERENCES EMPLOYE(id_employe);

ALTER TABLE HISTORIQUE_STATUT_DEMANDE
    ADD CONSTRAINT fk_histo_demande_acteur FOREIGN KEY (id_acteur) REFERENCES EMPLOYE(id_employe);

ALTER TABLE HISTORIQUE_STATUT_CANDIDATURE
    ADD CONSTRAINT fk_histo_candidature_acteur FOREIGN KEY (id_acteur) REFERENCES EMPLOYE(id_employe);

ALTER TABLE INTERVENANT_ENTRETIEN
    ADD CONSTRAINT fk_intervenant_employe FOREIGN KEY (id_employe) REFERENCES EMPLOYE(id_employe);

ALTER TABLE EVALUATION_CANDIDAT 
    ADD CONSTRAINT fk_evaluation_evaluateur FOREIGN KEY (id_evaluateur) REFERENCES EMPLOYE(id_employe);

ALTER TABLE HISTORIQUE_STATUT_PROPOSITION
    ADD CONSTRAINT fk_histo_prop_acteur FOREIGN KEY (id_acteur) REFERENCES EMPLOYE(id_employe);

-- ============================================================================
-- BLOC 4 : CONTRATS, HISTORIQUE ET AFFECTATIONS
-- ============================================================================

CREATE TABLE CONTRAT (
    id_contrat SERIAL PRIMARY KEY,
    numero_contrat VARCHAR(50) UNIQUE NOT NULL,
    id_employe INTEGER NOT NULL REFERENCES EMPLOYE(id_employe),
    type_contrat type_contrat_enum NOT NULL DEFAULT 'CE',  -- par defaut CE (essai)
    date_debut DATE NOT NULL,
    date_fin DATE,                           -- NULL strictement ssi CDI
    salaire_brut INTEGER NOT NULL CHECK (salaire_brut > 0),
    statut statut_contrat NOT NULL DEFAULT 'CREE',
    -- Trace d'historique : lien avec le contrat precedent si renouvellement
    id_contrat_precedent INTEGER,
    -- Tracabilite des acteurs du contrat (Defaut 11)
    id_createur INTEGER NOT NULL REFERENCES EMPLOYE(id_employe),
    id_signataire_employeur INTEGER REFERENCES EMPLOYE(id_employe),
    date_signature DATE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_dates_contrat CHECK (date_fin IS NULL OR date_fin >= date_debut),
    CONSTRAINT chk_contrat_non_reflexif CHECK (id_contrat_precedent IS NULL OR id_contrat_precedent <> id_contrat),
    -- Defaut 3 : CDI <=> date_fin IS NULL (et CE/CDD ont obligatoirement une date_fin)
    CONSTRAINT chk_contrat_fin_selon_type CHECK ((type_contrat = 'CDI') = (date_fin IS NULL)),
    -- Signature obligatoire des lors que le contrat est actif / termine
    CONSTRAINT chk_contrat_signature CHECK (
        (statut IN ('SIGNE', 'RENOUVELE', 'TERMINE', 'ROMPU')) = (date_signature IS NOT NULL AND id_signataire_employeur IS NOT NULL)
    ),
    -- Cles pour garantir que id_contrat_precedent appartient au MEME employe et renouvellement unique (Defaut 3)
    CONSTRAINT uq_contrat_id_employe UNIQUE (id_contrat, id_employe),
    CONSTRAINT uq_contrat_successeur UNIQUE (id_contrat_precedent),
    CONSTRAINT fk_contrat_precedent_meme_employe 
        FOREIGN KEY (id_contrat_precedent, id_employe) REFERENCES CONTRAT(id_contrat, id_employe),
    -- RG4-02 / Defaut 2 : Non-chevauchement des periodes contractuelles d'un employe
    CONSTRAINT ex_contrat_sans_chevauchement 
        EXCLUDE USING gist (id_employe WITH =, daterange(date_debut, date_fin, '[]') WITH &&)
);

CREATE TABLE AFFECTATION (
    id_affectation SERIAL PRIMARY KEY,
    id_employe INTEGER NOT NULL REFERENCES EMPLOYE(id_employe),
    id_poste INTEGER NOT NULL REFERENCES POSTE(id_poste),
    -- Normalisation 3NF : id_departement supprime car porte par le poste
    date_debut DATE NOT NULL,
    date_fin DATE,                           -- NULL ssi affectation actuelle
    CONSTRAINT chk_dates_affectation CHECK (date_fin IS NULL OR date_fin >= date_debut),
    -- Defaut 1 : Suppression de la colonne redondante est_actuelle et exclusion stricte des chevauchements
    CONSTRAINT ex_affectation_sans_chevauchement 
        EXCLUDE USING gist (id_employe WITH =, daterange(date_debut, date_fin, '[]') WITH &&)
);

-- ============================================================================
-- BLOC 5 : ONBOARDING, CHECKLIST & GESTION DES DOCUMENTS
-- ============================================================================

CREATE TABLE ONBOARDING (
    id_onboarding SERIAL PRIMARY KEY,
    id_employe INTEGER UNIQUE NOT NULL REFERENCES EMPLOYE(id_employe),
    date_debut DATE NOT NULL,
    complet BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE ONBOARDING_TACHE (
    id_tache SERIAL PRIMARY KEY,
    id_onboarding INTEGER NOT NULL REFERENCES ONBOARDING(id_onboarding),
    libelle VARCHAR(150) NOT NULL,           -- "Badge", "Compte informatique", "Contrat signe"...
    ordre INTEGER NOT NULL DEFAULT 1 CHECK (ordre > 0),
    date_echeance DATE,                      -- RG4-06
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE, -- RG4-07
    statut statut_tache NOT NULL DEFAULT 'A_FAIRE',
    id_responsable INTEGER REFERENCES EMPLOYE(id_employe),
    date_realisation TIMESTAMP,
    -- Defaut 7 : unicite de l'ordre et coherence statut FAIT <=> date_realisation
    CONSTRAINT uq_tache_ordre UNIQUE (id_onboarding, ordre),
    CONSTRAINT chk_tache_realisation CHECK ((statut = 'FAIT') = (date_realisation IS NOT NULL))
);

CREATE TABLE DOCUMENT (
    id_document SERIAL PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    type_document type_document_enum NOT NULL,
    fichier_url VARCHAR(255) NOT NULL,
    id_personne INTEGER REFERENCES PERSONNE(id_personne),
    id_candidature INTEGER REFERENCES CANDIDATURE(id_candidature), -- CV / LM candidature
    id_employe INTEGER REFERENCES EMPLOYE(id_employe),
    id_contrat INTEGER REFERENCES CONTRAT(id_contrat),
    id_createur INTEGER REFERENCES PERSONNE(id_personne),         -- Auteur du televersement
    date_televersement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Garde-fou Regle 12 : Au moins une cle de rattachement doit etre non-nulle
    CONSTRAINT chk_document_rattachement CHECK (
        id_personne IS NOT NULL OR id_employe IS NOT NULL OR id_contrat IS NOT NULL OR id_candidature IS NOT NULL
    )
);

-- ============================================================================
-- TRANSVERSE : COMPTES UTILISATEURS, MULTI-ROLES ET JOURNALISATION DE SECURITE
-- ============================================================================

CREATE TABLE COMPTE_UTILISATEUR (
    id_utilisateur SERIAL PRIMARY KEY,
    id_personne INTEGER UNIQUE NOT NULL REFERENCES PERSONNE(id_personne),
    identifiant VARCHAR(50) UNIQUE NOT NULL,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Multi-roles : permet a un utilisateur d'etre a la fois Manager et RH (Defaut 12)
CREATE TABLE COMPTE_ROLE (
    id_utilisateur INTEGER NOT NULL REFERENCES COMPTE_UTILISATEUR(id_utilisateur) ON DELETE CASCADE,
    role role_utilisateur NOT NULL,
    date_attribution TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_utilisateur, role)
);

-- Journalisation des consultations et actions sur les donnees sensibles (Section 10 de conception)
CREATE TABLE JOURNAL_ACCES (
    id_journal SERIAL PRIMARY KEY,
    id_utilisateur INTEGER NOT NULL REFERENCES COMPTE_UTILISATEUR(id_utilisateur),
    type_action VARCHAR(50) NOT NULL,        -- 'CONSULTATION', 'MODIFICATION', 'EXPORT'
    ressource_consultee VARCHAR(100) NOT NULL, -- 'SALAIRE_CONTRAT', 'EVALUATION_CANDIDAT', 'DOSSIER_EMPLOYE'
    id_ressource INTEGER,
    date_acces TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    adresse_ip VARCHAR(45)
);

-- ============================================================================
-- INDEX D'OPTIMISATION DES CLES ETRANGERES (Performances de jointures)
-- ============================================================================

CREATE INDEX idx_poste_departement ON POSTE(id_departement);
CREATE INDEX idx_demande_poste ON DEMANDE_RECRUTEMENT(id_poste);
CREATE INDEX idx_demande_demandeur ON DEMANDE_RECRUTEMENT(id_demandeur);
CREATE INDEX idx_validation_demande ON VALIDATION_DEMANDE(id_demande);
CREATE INDEX idx_candidature_offre ON CANDIDATURE(id_offre);
CREATE INDEX idx_candidature_personne ON CANDIDATURE(id_personne);
CREATE INDEX idx_entretien_candidature ON ENTRETIEN(id_candidature);
CREATE INDEX idx_evaluation_entretien ON EVALUATION_CANDIDAT(id_entretien);
CREATE INDEX idx_evaluation_evaluateur ON EVALUATION_CANDIDAT(id_evaluateur);
CREATE INDEX idx_employe_personne ON EMPLOYE(id_personne);
CREATE INDEX idx_employe_manager ON EMPLOYE(id_manager);
CREATE INDEX idx_contrat_employe ON CONTRAT(id_employe);
CREATE INDEX idx_affectation_employe ON AFFECTATION(id_employe);
CREATE INDEX idx_affectation_poste ON AFFECTATION(id_poste);
CREATE INDEX idx_onboarding_tache_onboarding ON ONBOARDING_TACHE(id_onboarding);
CREATE INDEX idx_document_personne ON DOCUMENT(id_personne);
CREATE INDEX idx_document_candidature ON DOCUMENT(id_candidature);
CREATE INDEX idx_document_employe ON DOCUMENT(id_employe);
CREATE INDEX idx_document_contrat ON DOCUMENT(id_contrat);
