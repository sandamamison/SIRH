-- ============================================================================
-- SCHEMA RELATIONNEL POSTGRESQL - SIRH (Gestion des Ressources Humaines)
-- Conception basee sur les specifications : SIRH.md et todo_sirh.md
-- ============================================================================

-- ----------------------------------------------------------------------------
-- TYPES ENUMERES POUR LES STATUTS ET TYPES METIERS
-- ----------------------------------------------------------------------------

CREATE TYPE statut_demande AS ENUM (
    'BROUILLON', 
    'EN_ATTENTE_MANAGER', 
    'EN_ATTENTE_RH', 
    'EN_ATTENTE_DIRECTION', 
    'VALIDEE', 
    'REJETEE'
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

CREATE TYPE statut_proposition AS ENUM (
    'EN_ATTENTE', 
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
    'SALARIE', 
    'MANAGER', 
    'RH', 
    'DIRECTION', 
    'ADMIN'
);

-- ============================================================================
-- BLOC 0 : IDENTITE COMMUNE (CANDIDAT != EMPLOYE, MAIS MEME PERSONNE PHYSIQUE)
-- ============================================================================

CREATE TABLE PERSONNE (
    id_personne SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(30),
    adresse TEXT,
    date_naissance DATE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- BLOC 1 : STRUCTURE ORGANISATIONNELLE & BESOIN EN PERSONNEL
-- ============================================================================

CREATE TABLE DEPARTEMENT (
    id_departement SERIAL PRIMARY KEY,
    code_departement VARCHAR(20) UNIQUE NOT NULL,
    nom_departement VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE POSTE (
    id_poste SERIAL PRIMARY KEY,
    code_poste VARCHAR(30) UNIQUE NOT NULL,
    titre VARCHAR(100) NOT NULL,
    description_mission TEXT,
    salaire_min INTEGER,
    salaire_max INTEGER,
    id_departement INTEGER NOT NULL REFERENCES DEPARTEMENT(id_departement)
);

CREATE TABLE DEMANDE_RECRUTEMENT (
    id_demande SERIAL PRIMARY KEY,
    id_poste INTEGER NOT NULL REFERENCES POSTE(id_poste),
    id_departement INTEGER NOT NULL REFERENCES DEPARTEMENT(id_departement),
    effectif_demande INTEGER NOT NULL DEFAULT 1 CHECK (effectif_demande > 0),
    motif TEXT NOT NULL,                     -- RG1: motif obligatoire (remplacement, creation...)
    budget_prevu INTEGER NOT NULL CHECK (budget_prevu > 0),
    date_souhaitee DATE NOT NULL,
    statut statut_demande NOT NULL DEFAULT 'BROUILLON',
    id_demandeur INTEGER NOT NULL REFERENCES PERSONNE(id_personne),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE VALIDATION_DEMANDE (
    id_validation SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL REFERENCES DEMANDE_RECRUTEMENT(id_demande),
    id_valideur INTEGER NOT NULL REFERENCES PERSONNE(id_personne),
    niveau_role role_utilisateur NOT NULL,   -- MANAGER, RH ou DIRECTION
    decision VARCHAR(20) NOT NULL CHECK (decision IN ('APPROUVE', 'REJETE')),
    commentaire TEXT,
    date_decision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- BLOC 2 : RECRUTEMENT
-- ============================================================================

CREATE TABLE OFFRE_EMPLOI (
    id_offre SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL REFERENCES DEMANDE_RECRUTEMENT(id_demande),
    id_poste INTEGER NOT NULL REFERENCES POSTE(id_poste),
    titre_offre VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    salaire_propose INTEGER,
    horaires_travail VARCHAR(100),           -- ex: "Lundi-Vendredi 8h-17h"
    lieu_travail VARCHAR(150) NOT NULL,
    date_publication DATE,
    date_cloture DATE,
    statut statut_offre NOT NULL DEFAULT 'BROUILLON'
);

CREATE TABLE CANDIDATURE (
    id_candidature SERIAL PRIMARY KEY,
    id_offre INTEGER NOT NULL REFERENCES OFFRE_EMPLOI(id_offre),
    id_personne INTEGER NOT NULL REFERENCES PERSONNE(id_personne),
    date_candidature TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cv_url VARCHAR(255),
    lettre_motivation TEXT,
    pretention_salariale INTEGER,
    accord_salaire BOOLEAN,                  -- salaire propose : ok / non
    remarque_salaire TEXT,
    statut statut_candidature NOT NULL DEFAULT 'RECUE',
    -- RG2: une seule candidature par offre et par personne
    CONSTRAINT uq_candidat_offre UNIQUE (id_offre, id_personne)
);

CREATE TABLE ENTRETIEN (
    id_entretien SERIAL PRIMARY KEY,
    id_candidature INTEGER NOT NULL REFERENCES CANDIDATURE(id_candidature),
    date_heure TIMESTAMP NOT NULL,
    lieu_ou_lien VARCHAR(255),
    type_entretien VARCHAR(50),              -- RH, TECHNIQUE, DIRECTION
    id_evaluateur INTEGER NOT NULL REFERENCES PERSONNE(id_personne)
);

CREATE TABLE EVALUATION_CANDIDAT (
    id_evaluation SERIAL PRIMARY KEY,
    id_entretien INTEGER NOT NULL REFERENCES ENTRETIEN(id_entretien),
    score_global INTEGER CHECK (score_global BETWEEN 0 AND 20),
    points_forts TEXT,
    points_faibles TEXT,
    avis VARCHAR(30) CHECK (avis IN ('FAVORABLE', 'RESERVE', 'DEFAVORABLE')),
    date_evaluation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    statut statut_proposition NOT NULL DEFAULT 'EN_ATTENTE'
);

CREATE TABLE EMPLOYE (
    id_employe SERIAL PRIMARY KEY,
    matricule VARCHAR(30) UNIQUE NOT NULL,   -- ex: EMP-2026-0001
    id_personne INTEGER UNIQUE NOT NULL REFERENCES PERSONNE(id_personne),
    date_embauche DATE NOT NULL,
    compte_bancaire VARCHAR(50),             -- RIB/IBAN (optionnel)
    numero_securite_sociale VARCHAR(50),
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================================================
-- BLOC 4 : CONTRATS, HISTORIQUE ET AFFECTATIONS
-- ============================================================================

CREATE TABLE CONTRAT (
    id_contrat SERIAL PRIMARY KEY,
    numero_contrat VARCHAR(50) UNIQUE NOT NULL,
    id_employe INTEGER NOT NULL REFERENCES EMPLOYE(id_employe),
    type_contrat type_contrat_enum NOT NULL DEFAULT 'CE',  -- par defaut CE (essai)
    date_debut DATE NOT NULL,
    date_fin DATE,                           -- NULL pour un CDI
    salaire_brut INTEGER NOT NULL CHECK (salaire_brut > 0),
    statut statut_contrat NOT NULL DEFAULT 'CREE',
    -- Trace d'historique : lien avec le contrat precedent si renouvellement
    id_contrat_precedent INTEGER REFERENCES CONTRAT(id_contrat),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_dates_contrat CHECK (date_fin IS NULL OR date_fin >= date_debut)
);

CREATE TABLE AFFECTATION (
    id_affectation SERIAL PRIMARY KEY,
    id_employe INTEGER NOT NULL REFERENCES EMPLOYE(id_employe),
    id_poste INTEGER NOT NULL REFERENCES POSTE(id_poste),
    id_departement INTEGER NOT NULL REFERENCES DEPARTEMENT(id_departement),
    date_debut DATE NOT NULL,
    date_fin DATE,                           -- NULL si affectation actuelle
    est_actuelle BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_dates_affectation CHECK (date_fin IS NULL OR date_fin >= date_debut)
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
    ordre INTEGER NOT NULL DEFAULT 1,
    statut statut_tache NOT NULL DEFAULT 'A_FAIRE',
    id_responsable INTEGER REFERENCES EMPLOYE(id_employe),
    date_realisation TIMESTAMP
);

CREATE TABLE DOCUMENT (
    id_document SERIAL PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    type_document VARCHAR(50) NOT NULL,      -- CONTRAT_SIGNE, CV, PIECE_IDENTITE, DIPLOME
    fichier_url VARCHAR(255) NOT NULL,
    id_personne INTEGER REFERENCES PERSONNE(id_personne),
    id_employe INTEGER REFERENCES EMPLOYE(id_employe),
    id_contrat INTEGER REFERENCES CONTRAT(id_contrat),
    date_televersement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- COMPTES UTILISATEURS & ROLES SYSTEME (TRANSVERSE)
-- ============================================================================

CREATE TABLE COMPTE_UTILISATEUR (
    id_utilisateur SERIAL PRIMARY KEY,
    id_personne INTEGER UNIQUE NOT NULL REFERENCES PERSONNE(id_personne),
    identifiant VARCHAR(50) UNIQUE NOT NULL,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role role_utilisateur NOT NULL DEFAULT 'SALARIE',
    actif BOOLEAN NOT NULL DEFAULT TRUE
);
