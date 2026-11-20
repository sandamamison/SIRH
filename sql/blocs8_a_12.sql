-- ====================================================================
-- BLOC 8: PAIE (Moteur de calcul, Bulletin, Heures Supp, Tranches IRSA)
-- BLOC 9: EVALUATION, COMPETENCES ET FORMATION
-- BLOC 10: CARRIERE ET MOBILITE
-- BLOC 11: DISCIPLINE
-- BLOC 12: RUPTURE ET OFFBOARDING
-- ====================================================================

-- 1. Extension de la table employes pour les besoins de la paie malgache
ALTER TABLE employes ADD COLUMN IF NOT EXISTS numero_cnaps VARCHAR(50);
ALTER TABLE employes ADD COLUMN IF NOT EXISTS classification VARCHAR(20) DEFAULT 'HC';
ALTER TABLE employes ADD COLUMN IF NOT EXISTS indice NUMERIC(10,2) DEFAULT 1733;

-- BLOC 8: PAIE
CREATE TABLE IF NOT EXISTS periode_paie (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL, -- ex: '2026-11'
    mois INT NOT NULL,
    annee INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'OUVERTE', -- OUVERTE, CLOTUREE
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bulletin_paie (
    id SERIAL PRIMARY KEY,
    periode_paie_id INT NOT NULL REFERENCES periode_paie(id),
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    salaire_base NUMERIC(15,2) NOT NULL,
    taux_journalier NUMERIC(15,2) NOT NULL,
    taux_horaire NUMERIC(15,2) NOT NULL,
    classification VARCHAR(20) DEFAULT 'HC',
    indice NUMERIC(10,2) DEFAULT 1733,
    heures_normales NUMERIC(6,2) DEFAULT 173.33,
    total_hs NUMERIC(15,2) DEFAULT 0,
    total_absence_deductible NUMERIC(15,2) DEFAULT 0,
    total_primes NUMERIC(15,2) DEFAULT 0,
    salaire_brut NUMERIC(15,2) NOT NULL,
    retenue_cnaps NUMERIC(15,2) NOT NULL,
    retenue_sanitaire NUMERIC(15,2) NOT NULL,
    total_irsa NUMERIC(15,2) NOT NULL,
    total_retenues NUMERIC(15,2) NOT NULL,
    autres_indemnites NUMERIC(15,2) DEFAULT 0,
    net_a_payer NUMERIC(15,2) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'VALIDE', -- BROUILLON, VALIDE, PAYE
    date_generation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bulletin_periode_employe UNIQUE(periode_paie_id, employe_id)
);

CREATE TABLE IF NOT EXISTS calcul_hs_semaine (
    id SERIAL PRIMARY KEY,
    bulletin_paie_id INT NOT NULL REFERENCES bulletin_paie(id) ON DELETE CASCADE,
    numero_semaine INT NOT NULL, -- 1, 2, 3, 4
    heures_30 NUMERIC(6,2) DEFAULT 0,
    heures_50 NUMERIC(6,2) DEFAULT 0,
    heures_weekend NUMERIC(6,2) DEFAULT 0,
    heures_ferie NUMERIC(6,2) DEFAULT 0,
    montant_30 NUMERIC(15,2) DEFAULT 0,
    montant_50 NUMERIC(15,2) DEFAULT 0,
    montant_weekend NUMERIC(15,2) DEFAULT 0,
    montant_ferie NUMERIC(15,2) DEFAULT 0,
    total_heures NUMERIC(6,2) DEFAULT 0,
    montant_semaine NUMERIC(15,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ligne_bulletin (
    id SERIAL PRIMARY KEY,
    bulletin_paie_id INT NOT NULL REFERENCES bulletin_paie(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    designation VARCHAR(255) NOT NULL,
    nombre NUMERIC(10,2),
    taux NUMERIC(15,2),
    montant NUMERIC(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- GAIN, RETENUE, COTISATION, IRSA, INFO
    ordre INT NOT NULL
);

CREATE TABLE IF NOT EXISTS variable_paie (
    id SERIAL PRIMARY KEY,
    periode_paie_id INT NOT NULL REFERENCES periode_paie(id),
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    type VARCHAR(50) NOT NULL, -- PRIME_RENDEMENT, PRIME_ANCIENNETE, HS, ABSENCE, RAPPEL, AUTRES_INDEMNITES
    libelle VARCHAR(255) NOT NULL,
    nombre NUMERIC(10,2) DEFAULT 0,
    taux NUMERIC(15,2) DEFAULT 0,
    montant NUMERIC(15,2) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- BLOC 9: EVALUATION, COMPETENCES ET FORMATION
CREATE TABLE IF NOT EXISTS competence (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS employe_competence (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    competence_id INT NOT NULL REFERENCES competence(id),
    niveau_acquis INT NOT NULL DEFAULT 1, -- 1 à 5
    niveau_requis INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS objectif_evaluation (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    cible VARCHAR(100),
    statut VARCHAR(30) DEFAULT 'EN_COURS' -- ATTEINT, NON_ATTEINT, EN_COURS
);

CREATE TABLE IF NOT EXISTS evaluation_rh (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    evaluateur_id BIGINT NOT NULL REFERENCES employes(id),
    date_evaluation DATE NOT NULL,
    note_globale NUMERIC(4,2),
    commentaires TEXT,
    besoins_formation TEXT,
    statut VARCHAR(30) DEFAULT 'VALIDEE'
);

CREATE TABLE IF NOT EXISTS formation (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    duree_heures INT NOT NULL,
    cout NUMERIC(15,2) DEFAULT 0,
    employe_id BIGINT REFERENCES employes(id),
    date_debut DATE,
    date_fin DATE,
    statut VARCHAR(30) DEFAULT 'PLANIFIEE', -- PLANIFIEE, EN_COURS, TERMINEE
    evaluation_post_formation TEXT
);

-- BLOC 10: CARRIERE ET MOBILITE
CREATE TABLE IF NOT EXISTS historique_affectation_carriere (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    poste_id INT NOT NULL REFERENCES poste(id),
    departement_id INT REFERENCES departement(id),
    date_debut DATE NOT NULL,
    date_fin DATE,
    motif VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS promotion (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    ancien_poste_id INT NOT NULL REFERENCES poste(id),
    nouveau_poste_id INT NOT NULL REFERENCES poste(id),
    date_effet DATE NOT NULL,
    augmentation_salaire NUMERIC(15,2) DEFAULT 0,
    motif TEXT,
    statut VARCHAR(30) DEFAULT 'VALIDEE'
);

CREATE TABLE IF NOT EXISTS mutation (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    ancien_departement_id INT REFERENCES departement(id),
    nouveau_departement_id INT REFERENCES departement(id),
    date_effet DATE NOT NULL,
    motif TEXT
);

CREATE TABLE IF NOT EXISTS historique_salaire (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    ancien_salaire NUMERIC(15,2) NOT NULL,
    nouveau_salaire NUMERIC(15,2) NOT NULL,
    date_changement DATE NOT NULL,
    motif VARCHAR(255)
);

-- BLOC 11: DISCIPLINE
CREATE TABLE IF NOT EXISTS incident_disciplinaire (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    date_incident DATE NOT NULL,
    motif VARCHAR(255) NOT NULL,
    description TEXT,
    gravite VARCHAR(30) DEFAULT 'MOYENNE', -- FAIBLE, MOYENNE, GRAVE, TRES_GRAVE
    statut VARCHAR(30) DEFAULT 'EN_COURS' -- DECLARE, EN_COURS, CLOTURE
);

CREATE TABLE IF NOT EXISTS procedure_disciplinaire (
    id SERIAL PRIMARY KEY,
    incident_id INT NOT NULL REFERENCES incident_disciplinaire(id) ON DELETE CASCADE,
    date_demande_explication DATE NOT NULL,
    demande_explication TEXT NOT NULL,
    date_reponse DATE,
    reponse_salarie TEXT,
    date_decision DATE,
    decision TEXT,
    statut VARCHAR(30) DEFAULT 'DEMANDE_ENVOYEE' -- DEMANDE_ENVOYEE, REPONSE_RECUE, DECIDEE, CLOTUREE
);

CREATE TABLE IF NOT EXISTS sanction_disciplinaire (
    id SERIAL PRIMARY KEY,
    procedure_id INT NOT NULL REFERENCES procedure_disciplinaire(id) ON DELETE CASCADE,
    type_sanction VARCHAR(50) NOT NULL, -- AVERTISSEMENT_VERBAL, AVERTISSEMENT_ECRIT, MISE_A_PIED, RETROGRADATION, LICENCIEMENT_DISCIPLINAIRE
    date_notification DATE NOT NULL,
    description TEXT,
    justificatif_url VARCHAR(255)
);

-- BLOC 12: RUPTURE ET OFFBOARDING
CREATE TABLE IF NOT EXISTS rupture_contrat (
    id SERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    contrat_id INT NOT NULL REFERENCES contrat(id),
    motif VARCHAR(50) NOT NULL, -- DEMISSION, LICENCIEMENT, FIN_CDD, RETRAITE, RUPTURE_CONVENTIONNELLE
    date_notification DATE NOT NULL,
    date_sortie_prevue DATE NOT NULL,
    date_sortie_effective DATE,
    preavis_effectue BOOLEAN DEFAULT true,
    jours_preavis INT DEFAULT 30,
    jours_conges_restants NUMERIC(5,2) DEFAULT 0,
    statut VARCHAR(30) DEFAULT 'EN_COURS' -- INITIÉE, PREAVIS_EN_COURS, TERMINEE
);

CREATE TABLE IF NOT EXISTS solde_tout_compte (
    id SERIAL PRIMARY KEY,
    rupture_id INT NOT NULL REFERENCES rupture_contrat(id) ON DELETE CASCADE,
    employe_id BIGINT NOT NULL REFERENCES employes(id),
    date_calcul DATE NOT NULL,
    salaire_dernier_mois NUMERIC(15,2) NOT NULL,
    indemnite_conges_payes NUMERIC(15,2) DEFAULT 0,
    indemnite_preavis NUMERIC(15,2) DEFAULT 0,
    indemnite_licenciement NUMERIC(15,2) DEFAULT 0,
    total_brut NUMERIC(15,2) NOT NULL,
    total_retenues NUMERIC(15,2) NOT NULL,
    net_solde NUMERIC(15,2) NOT NULL,
    recu_signe BOOLEAN DEFAULT false
);
