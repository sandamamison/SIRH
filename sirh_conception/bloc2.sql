-- ============================================================
-- BLOC 2 : RECRUTEMENT
-- A executer apres bloc1.sql
-- ============================================================

-- 1. OFFRES D'EMPLOI
CREATE TABLE offreEmploi (
    id SERIAL PRIMARY KEY,
    demandeRecrutementId INTEGER NOT NULL UNIQUE
        REFERENCES demandeRecrutement(id),
    titreOffre VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    salairePropose INTEGER CHECK (salairePropose > 0),
    horairesTravail VARCHAR(100),
    lieuTravail VARCHAR(150) NOT NULL,
    datePublication DATE,
    dateCloture DATE,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON'
        CHECK (statut IN (
            'BROUILLON', 'PUBLIEE', 'CLOTUREE', 'ANNULEE'
        )),
    CHECK (
        dateCloture IS NULL
        OR datePublication IS NULL
        OR dateCloture >= datePublication
    ),
    CHECK (
        statut <> 'PUBLIEE'
        OR datePublication IS NOT NULL
    ),
    CHECK (
        statut <> 'CLOTUREE'
        OR dateCloture IS NOT NULL
    )
);

-- 2. CANDIDATURES
CREATE TABLE candidature (
    id SERIAL PRIMARY KEY,
    offreEmploiId INTEGER NOT NULL
        REFERENCES offreEmploi(id),
    personneId INTEGER NOT NULL,
    dateCandidature TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cvUrl VARCHAR(255),
    lettreMotivation TEXT,
    pretentionSalariale INTEGER
        CHECK (pretentionSalariale > 0),
    accordSalaire BOOLEAN,
    remarqueSalaire TEXT,
    statut VARCHAR(30) NOT NULL DEFAULT 'RECUE'
        CHECK (statut IN (
            'RECUE',
            'PRESELECTIONNEE',
            'EN_ENTRETIEN',
            'RETENUE',
            'REJETEE'
        )),
    UNIQUE (offreEmploiId, personneId)
);

-- 3. HISTORIQUE DES STATUTS DES CANDIDATURES
CREATE TABLE historiqueStatutCandidature (
    id SERIAL PRIMARY KEY,
    candidatureId INTEGER NOT NULL
        REFERENCES candidature(id),
    ancienStatut VARCHAR(30),
    nouveauStatut VARCHAR(30) NOT NULL
        CHECK (nouveauStatut IN (
            'RECUE',
            'PRESELECTIONNEE',
            'EN_ENTRETIEN',
            'RETENUE',
            'REJETEE'
        )),
    employeActeurId INTEGER NOT NULL,
    commentaire TEXT,
    dateTransition TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. ENTRETIENS
CREATE TABLE entretien (
    id SERIAL PRIMARY KEY,
    candidatureId INTEGER NOT NULL
        REFERENCES candidature(id),
    dateHeure TIMESTAMP NOT NULL,
    lieuOuLien VARCHAR(255),
    typeEntretien VARCHAR(30) NOT NULL DEFAULT 'RH'
        CHECK (typeEntretien IN (
            'RH', 'TECHNIQUE', 'DIRECTION', 'TEST'
        )),
    statut VARCHAR(20) NOT NULL DEFAULT 'PLANIFIE'
        CHECK (statut IN (
            'PLANIFIE', 'EFFECTUE', 'ANNULE', 'REPORTE'
        ))
);

-- 5. INTERVENANTS AUX ENTRETIENS
CREATE TABLE intervenantEntretien (
    id SERIAL PRIMARY KEY,
    entretienId INTEGER NOT NULL
        REFERENCES entretien(id) ON DELETE CASCADE,
    employeId INTEGER NOT NULL,
    roleIntervenant VARCHAR(50) NOT NULL DEFAULT 'EXAMINATEUR',
    UNIQUE (entretienId, employeId)
);

-- 6. EVALUATIONS DES CANDIDATS
CREATE TABLE evaluationCandidat (
    id SERIAL PRIMARY KEY,
    entretienId INTEGER NOT NULL
        REFERENCES entretien(id),
    employeEvaluateurId INTEGER NOT NULL,
    scoreGlobal INTEGER
        CHECK (scoreGlobal BETWEEN 0 AND 20),
    pointsForts TEXT,
    pointsFaibles TEXT,
    avis VARCHAR(20) NOT NULL
        CHECK (avis IN (
            'FAVORABLE', 'RESERVE', 'DEFAVORABLE'
        )),
    dateEvaluation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (entretienId, employeEvaluateurId)
);

-- ============================================================
-- INDEX DES CLES ETRANGERES
-- ============================================================

CREATE INDEX idx_candidature_offre
    ON candidature(offreEmploiId);

CREATE INDEX idx_candidature_personne
    ON candidature(personneId);

CREATE INDEX idx_historique_candidature
    ON historiqueStatutCandidature(candidatureId);

CREATE INDEX idx_entretien_candidature
    ON entretien(candidatureId);

CREATE INDEX idx_intervenant_entretien
    ON intervenantEntretien(entretienId);

CREATE INDEX idx_intervenant_employe
    ON intervenantEntretien(employeId);

CREATE INDEX idx_evaluation_entretien
    ON evaluationCandidat(entretienId);

CREATE INDEX idx_evaluation_evaluateur
    ON evaluationCandidat(employeEvaluateurId);
